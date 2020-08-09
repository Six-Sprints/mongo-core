package com.sixsprints.core.controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sixsprints.core.annotation.Authenticated;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.BulkUpdateInfo;
import com.sixsprints.core.dto.ImportResponseWrapper;
import com.sixsprints.core.enums.AccessPermission;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;

public abstract class AbstractCrudController<T extends AbstractMongoEntity, DTO>
  extends AbstractReadController<T, DTO> {

  private GenericCrudService<T> service;

  private GenericTransformer<T, DTO> mapper;

  public AbstractCrudController(GenericCrudService<T> service, GenericTransformer<T, DTO> mapper) {
    super(service, mapper);
    this.service = service;
    this.mapper = mapper;
  }

  @PutMapping
  @Authenticated(access = AccessPermission.UPDATE)
  public ResponseEntity<?> patch(@RequestBody @Validated DTO dto, @RequestParam String propChanged)
    throws BaseException {
    T domain = mapper.toDomain(dto);
    return RestUtil.successResponse(service.patchUpdate(domain.getId(), domain, propChanged));
  }

  @PutMapping("/update")
  @Authenticated(access = AccessPermission.UPDATE)
  public ResponseEntity<?> update(@RequestBody @Validated DTO dto) throws BaseException {
    T domain = mapper.toDomain(dto);
    return RestUtil.successResponse(service.update(domain.getId(), domain));
  }

  @PostMapping
  @Authenticated(access = AccessPermission.CREATE)
  public ResponseEntity<RestResponse<DTO>> add(@RequestBody @Validated DTO dto)
    throws BaseException {
    return RestUtil.successResponse(mapper.toDto(service.create(mapper.toDomain(dto))));
  }

  @PostMapping("/delete")
  @Authenticated(access = AccessPermission.DELETE)
  public ResponseEntity<?> delete(@RequestBody List<String> ids) throws EntityNotFoundException {
    service.delete(ids);
    return RestUtil.successResponse(null);
  }

  @PostMapping("/import")
  @Authenticated(access = AccessPermission.UPDATE)
  public ResponseEntity<?> upload(@RequestParam(value = "file", required = true) MultipartFile file,
    Locale locale) throws IOException, BaseException {
    ImportResponseWrapper<DTO> importResponseWrapper = service.importData(file.getInputStream(), locale);
    List<BulkUpdateInfo<T>> updateInfo = service.updateAll(mapper.toDomain(importResponseWrapper.getData()));
    service.saveImportLogs(importResponseWrapper, updateInfo);
    return RestUtil.successResponse(importResponseWrapper.getImportLogDetails());
  }

}
