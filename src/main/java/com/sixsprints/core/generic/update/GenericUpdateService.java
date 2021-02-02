package com.sixsprints.core.generic.update;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.BulkUpdateInfo;
import com.sixsprints.core.dto.IGenericExcelImport;
import com.sixsprints.core.dto.ImportLogDetailsDto;
import com.sixsprints.core.enums.ImportOperation;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.transformer.GenericMapper;

import cn.afterturn.easypoi.excel.entity.ImportParams;

public interface GenericUpdateService<T extends AbstractMongoEntity> {

  T update(String id, T domain) throws EntityNotFoundException, EntityAlreadyExistsException;

  T upsert(T domain) throws EntityInvalidException;

  T patchUpdate(String id, T domain, String propChanged) throws EntityNotFoundException, EntityAlreadyExistsException;

  List<BulkUpdateInfo<T>> bulkUpsert(List<T> list);

  <V> void saveImportLogs(Map<ImportOperation, ImportLogDetailsDto> importResponseWrapper,
    List<ImportLogDetailsDto> collection);

  <E extends IGenericExcelImport> Map<ImportOperation, ImportLogDetailsDto> importData(InputStream inputStream,
    GenericMapper<T, E> importMapper) throws Exception;

  <E extends IGenericExcelImport> Map<ImportOperation, ImportLogDetailsDto> importData(List<E> data,
    GenericMapper<T, E> importMapper) throws BaseException;

  <E extends IGenericExcelImport> List<E> importDataPreview(InputStream inputStream) throws Exception;

  <E extends IGenericExcelImport> List<E> importDataPreview(InputStream inputStream, ImportParams params)
    throws Exception;

}
