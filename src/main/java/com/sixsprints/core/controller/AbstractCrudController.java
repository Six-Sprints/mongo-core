package com.sixsprints.core.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.sixsprints.core.annotation.Authenticated;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.enums.AccessPermission;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.core.transformer.GenericMapper;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;

public abstract class AbstractCrudController<T extends AbstractMongoEntity, DTO>
  extends AbstractReadController<T, DTO> {

  private GenericCrudService<T> service;

  private GenericMapper<T, DTO> mapper;

  public AbstractCrudController(GenericCrudService<T> service, GenericMapper<T, DTO> mapper) {
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

}
