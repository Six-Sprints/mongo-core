package com.sixsprints.core.controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.ImportResponseWrapper;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.core.utils.AppConstants;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;

public abstract class AbstractCrudController<T extends AbstractMongoEntity, DTO> {

  private GenericCrudService<T> service;

  private GenericTransformer<T, DTO> mapper;

  public AbstractCrudController(GenericCrudService<T> service, GenericTransformer<T, DTO> mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @PutMapping
  public ResponseEntity<?> patch(@RequestBody @Validated DTO dto, @RequestParam String propChanged)
    throws BaseException {
    T domain = mapper.toDomain(dto);
    return RestUtil.successResponse(service.patchUpdate(domain.getId(), domain, propChanged));
  }

  @PostMapping
  public ResponseEntity<RestResponse<DTO>> add(@RequestBody @Validated DTO dto)
    throws BaseException {
    return RestUtil.successResponse(mapper.toDto(service.create(mapper.toDomain(dto))));
  }

  @GetMapping("/{slug}")
  public ResponseEntity<RestResponse<DTO>> findBySlug(@PathVariable String slug)
    throws EntityNotFoundException {
    return RestUtil.successResponse(mapper.toDto(service.findBySlug(slug)));
  }

  @PostMapping("/search")
  public ResponseEntity<RestResponse<PageDto<DTO>>> filter(@RequestBody FilterRequestDto filterRequestDto) {
    return RestUtil.successResponse(mapper.pageEntityToPageDtoDto(service.filter(filterRequestDto)));
  }

  @PostMapping("/column/master")
  public ResponseEntity<RestResponse<List<?>>> getDistinctValues(@RequestParam String column,
    @RequestBody FilterRequestDto filterRequestDto) {
    return RestUtil.successResponse(service.distinctColumnValues(column, filterRequestDto));
  }

  @PostMapping("/delete")
  public ResponseEntity<?> delete(@RequestBody List<String> ids) throws EntityNotFoundException {
    service.delete(ids);
    return RestUtil.successResponse(null);
  }

  @PostMapping(value = "/export", produces = "text/csv")
  public void download(
    @RequestBody FilterRequestDto filterRequestDto, HttpServletResponse response, Locale locale)
    throws BaseException, IOException {
    service.exportData(mapper, filterRequestDto, response.getWriter(), locale);
  }

  @PostMapping("/import")
  public ResponseEntity<?> upload(@RequestParam(value = "file", required = true) MultipartFile file,
    Locale locale) throws IOException, BaseException {

    ImportResponseWrapper<DTO> importResponseWrapper = service.importData(file.getInputStream(), locale);
    service.updateAll(mapper.toDomain(importResponseWrapper.getData()));

    // return import process analysis
    return RestUtil.successResponse(null,
      String.format(AppConstants.CSV_IMPORT_MESSAGE, importResponseWrapper.getImportLogDetails().getSuccessRowCount(),
        importResponseWrapper.getImportLogDetails().getWarningRowCount(),
        importResponseWrapper.getImportLogDetails().getErrorRowCount()));
  }

}
