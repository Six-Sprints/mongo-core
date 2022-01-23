package com.sixsprints.core.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sixsprints.core.annotation.Authenticated;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.IGenericExcelImport;
import com.sixsprints.core.dto.ImportLogDetailsDto;
import com.sixsprints.core.enums.AccessPermission;
import com.sixsprints.core.enums.ImportOperation;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.core.transformer.GenericMapper;
import com.sixsprints.core.utils.DateUtil;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;

public abstract class AbstractCrudWithExcelSupportController<T extends AbstractMongoEntity, SD, CD, EI extends IGenericExcelImport, EE>
  extends AbstractCrudController<T, SD, CD> {

  @Autowired
  private DateUtil dateUtil;

  private GenericCrudService<T> crudService;

  private GenericMapper<T, EI> importMapper;

  private GenericMapper<T, EE> exportMapper;

  public AbstractCrudWithExcelSupportController(GenericCrudService<T> crudService, GenericMapper<T, SD> searchMapper,
    GenericMapper<T, CD> crudMapper, GenericMapper<T, EI> importMapper, GenericMapper<T, EE> exportMapper) {
    super(crudService, searchMapper, crudMapper);
    this.crudService = crudService;
    this.importMapper = importMapper;
    this.exportMapper = exportMapper;
  }

  @PostMapping("/import/instant")
  @Authenticated(access = AccessPermission.UPDATE)
  public ResponseEntity<?> upload(@RequestParam(value = "file", required = true) MultipartFile file) throws Exception {
    Map<ImportOperation, ImportLogDetailsDto> importResponseWrapper = crudService.importData(file.getInputStream(),
      importMapper);
    crudService.saveImportLogs(importResponseWrapper, new ArrayList<>(importResponseWrapper.values()));
    return RestUtil.successResponse(importResponseWrapper);
  }

  @PostMapping("/import/preview")
  @Authenticated(access = AccessPermission.UPDATE)
  public ResponseEntity<RestResponse<List<EI>>> importPreview(
    @RequestParam(value = "file", required = true) MultipartFile file) throws Exception {

    if (file.isEmpty()) {
      throw new IllegalArgumentException();
    }
    List<EI> list = crudService.importDataPreview(file.getInputStream());
    return RestUtil.successResponse(list);
  }

  @PostMapping("/import")
  @Authenticated(access = AccessPermission.UPDATE)
  public ResponseEntity<RestResponse<Map<ImportOperation, ImportLogDetailsDto>>> bulkUpsert(
    @RequestBody @Validated List<EI> dtos) throws BaseException {
    return RestUtil.successResponse(crudService.importData(dtos, importMapper));
  }

  @PostMapping(value = "/export")
  @Authenticated(access = AccessPermission.READ)
  public void download(
    @RequestBody FilterRequestDto filterRequestDto, HttpServletResponse response)
    throws BaseException, IOException {
    setResponseForExcelDownload(response, entityName());
    crudService.exportData(exportMapper, filterRequestDto, response.getOutputStream());
  }

  @GetMapping("/import/preview/fields")
  @Authenticated(access = AccessPermission.READ)
  public ResponseEntity<RestResponse<List<FieldDto>>> importPreviewFields() {
    return RestUtil.successResponse(localise(importDtoFields()));
  }

  protected List<FieldDto> importDtoFields() {
    return new ArrayList<>();
  }

  protected String entityName() {
    return "data";
  }

  protected void setResponseForExcelDownload(HttpServletResponse response, String entityName) {
    response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.parseMediaType(
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").toString());
    response.addHeader(HttpHeaders.CONTENT_DISPOSITION,
      "attachment; filename=" + entityName + "_"
        + dateUtil.dateToStringWithFormat(dateUtil.now().toDate(), "yyyyMMdd_HHmmss")
        + ".xlsx");
    response.setHeader("Expires:", "0"); // eliminates browser caching
  }

  protected void setResponseForCsvDownload(HttpServletResponse response, String entityName) {
    response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.parseMediaType("text/csv").toString());
    response.addHeader(HttpHeaders.CONTENT_DISPOSITION,
      "attachment; filename=" + entityName + "_"
        + dateUtil.dateToStringWithFormat(dateUtil.now().toDate(), "yyyyMMdd_HHmmss")
        + ".csv");
    response.setContentType("text/csv;charset=UTF-8");
    response.setHeader("Expires:", "0"); // eliminates browser caching
  }

  protected void setResponseForZipFileDownload(HttpServletResponse response, String entityName) {
    response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.parseMediaType("application/zip").toString());
    response.addHeader(HttpHeaders.CONTENT_DISPOSITION,
      "attachment; filename=" + entityName
        + dateUtil.dateToStringWithFormat(dateUtil.now().toDate(), "yyyyMMdd_HHmmss")
        + ".zip");
    response.setHeader("Expires:", "0"); // eliminates browser caching
  }

}
