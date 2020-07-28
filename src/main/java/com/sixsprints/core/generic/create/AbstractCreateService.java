package com.sixsprints.core.generic.create;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.dozer.CsvDozerBeanReader;
import org.supercsv.io.dozer.ICsvDozerBeanReader;
import org.supercsv.prefs.CsvPreference;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.dto.ImportLogDetails;
import com.sixsprints.core.dto.ImportResponseWrapper;
import com.sixsprints.core.dto.UploadError;
import com.sixsprints.core.enums.UploadErrorType;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.generic.delete.AbstractDeleteService;
import com.sixsprints.core.utils.CellProcessorUtil;
import com.sixsprints.core.utils.FieldMappingUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCreateService<T extends AbstractMongoEntity> extends AbstractDeleteService<T>
  implements GenericCreateService<T> {

  @Override
  public T save(T entity) {
    generateSlugIfRequired(entity);
    preSave(entity);
    entity = repository().save(entity);
    postSave(entity);
    return entity;
  }

  @Override
  public List<T> saveAll(List<T> entities) {
    generateSlugIfRequired(entities);
    return repository().saveAll(entities);
  }

  @Override
  public List<T> saveAllWithHooks(List<T> entities) {
    List<T> list = new ArrayList<>();
    for (T entity : entities) {
      list.add(save(entity));
    }
    return list;
  }

  @Override
  public T create(T domain) throws EntityAlreadyExistsException, EntityInvalidException {
    preCreate(domain);
    if (isInvalid(domain)) {
      throw invalidException(domain);
    }
    T fromDB = findDuplicate(domain);
    if (fromDB != null) {
      if (fromDB.getActive()) {
        throw alreadyExistsException(fromDB);
      }
      delete(fromDB);
    }
    domain = save(domain);
    postCreate(domain);
    return domain;
  }

  public <DTO> ImportResponseWrapper<DTO> importData(InputStream inputStream, Locale locale)
    throws IOException, BaseException {

    @SuppressWarnings("unchecked")
    Class<DTO> classType = (Class<DTO>) metaData().getDtoClassType();

    log.info("Import request received for {}", classType.getSimpleName());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    IOUtils.copy(inputStream, baos);
    byte[] bytes = baos.toByteArray();
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    List<FieldDto> fields = metaData().getFields();
    String encoding = checkEncoding(bais, fields);
    List<DTO> data = new ArrayList<>();
    ICsvDozerBeanReader beanReader = null;
    List<UploadError> errors = new ArrayList<>();
    List<String> unknownErrors = new ArrayList<>();
    String[] firstLine;
    try {
      beanReader = new CsvDozerBeanReader(new InputStreamReader(bais, encoding), CsvPreference.STANDARD_PREFERENCE);
      firstLine = beanReader.getHeader(true);
      String[] mappings = readHeader(locale, beanReader, fields, firstLine);
      beanReader.configureBeanMapping(classType, mappings);
      CellProcessor[] cellProcessors = cellProcessors(fields);
      DTO domain = null;
      while (true) {
        try {
          domain = beanReader.read(classType, cellProcessors);
          if (domain == null) {
            break;
          }
          data.add(domain);
        } catch (SuperCsvException e) {
          log.warn("(Row, Col): ({}, {}). {}", e.getCsvContext().getRowNumber(), e.getCsvContext().getColumnNumber(),
            e.getMessage());
          errors
            .add(UploadError.builder().col(e.getCsvContext().getColumnNumber()).row(e.getCsvContext().getRowNumber())
              .message(e.getMessage()).type(UploadErrorType.ERROR.getDisplayName()).cellLocation(CellProcessorUtil
                .toExcelCellNotation(e.getCsvContext().getRowNumber(), e.getCsvContext().getColumnNumber()))
              .build());

        } catch (Exception ex) {
          log.error(ex.getMessage(), ex);
          unknownErrors.add(ex.getMessage());
        }
      }
    } finally {
      if (beanReader != null) {
        beanReader.close();
      }
      if (inputStream != null) {
        inputStream.close();
      }
    }

    int errorSize = errors.stream().filter(err -> UploadErrorType.ERROR.getDisplayName().equals(err.getType()))
      .mapToInt(e -> 1).sum();

    log.info("Processed {} records. Found {} validation errors. Found {} unknown errors.",
      errorSize + unknownErrors.size() + data.size(), errorSize, unknownErrors.size());

    Collections.sort(errors);

    ImportLogDetails log = ImportLogDetails.builder().errors(errors).unknownErrors(unknownErrors)
      .errorRowCount(errorSize)
      .successRowCount(data.size()).warningRowCount(errors.size() - errorSize)
      .totalRowCount(errorSize + unknownErrors.size() + data.size())
      .entity(metaData().getEntityName()).build();

    return ImportResponseWrapper.<DTO>builder().data(data).importLogDetails(log).firstLine(firstLine).build();
  }

  private CellProcessor[] cellProcessors(List<FieldDto> fields) {
    Map<String, CellProcessor> map = importCellProcessors(fields);
    return CellProcessorUtil.importProcessors(fields, map, mongo);
  }

  protected Map<String, CellProcessor> importCellProcessors(List<FieldDto> fields) {
    return new HashMap<>();
  }

  protected String[] readHeader(Locale locale, ICsvDozerBeanReader beanReader, List<FieldDto> fields,
    String[] firstLine) {
    return FieldMappingUtil.genericMappings(fields);
  }

  protected String checkEncoding(ByteArrayInputStream bais, List<FieldDto> fields) throws IOException {
    ICsvDozerBeanReader beanReader = null;
    String name = fields.get(0).getDisplayName();
    String encodings[] = supportedEncodings();
    for (String encoding : encodings) {
      try {
        bais.reset();
        int i = 0;
        beanReader = new CsvDozerBeanReader(new InputStreamReader(bais, encoding), CsvPreference.STANDARD_PREFERENCE);
        String[] header = beanReader.getHeader(true);
        while (i++ < 3) {
          log.info("Checking {}, {}", name, header[0]);
          if (StringUtils.isNotBlank(header[0])
            && (StringUtils.contains(name, header[0]) || StringUtils.contains(header[0], name))) {
            bais.reset();
            return encoding;
          }
          header = beanReader.getHeader(false);
        }
      } catch (Exception ex) {

      } finally {
        if (beanReader != null) {
          beanReader.close();
        }
      }
    }
    throw new IllegalArgumentException("encoding.error");
  }

  protected String[] supportedEncodings() {
    return new String[] { "utf-8" };
  }

}
