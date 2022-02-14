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

/**
 * 
 * @param <T>  - Domain Class Type
 * @param <SD> - Search DTO Class Type
 * @param <DD> - Detailed DTO Class Type
 * @param <CD> - CRUD DTO class Type
 */
public abstract class AbstractCrudController<T extends AbstractMongoEntity, SD, DD, CD>
  extends AbstractReadController<T, SD, DD> {

  private GenericCrudService<T> crudService;

  private GenericMapper<T, CD> crudMapper;

  public AbstractCrudController(GenericCrudService<T> crudService, GenericMapper<T, SD> searchMapper,
    GenericMapper<T, DD> detailMapper, GenericMapper<T, CD> crudMapper) {
    super(crudService, searchMapper, detailMapper);
    this.crudService = crudService;
    this.crudMapper = crudMapper;
  }

  @PutMapping
  @Authenticated(access = AccessPermission.UPDATE)
  public ResponseEntity<?> patch(@RequestBody @Validated CD dto, @RequestParam String propChanged)
    throws BaseException {
    T domain = crudMapper.toDomain(dto);
    return RestUtil.successResponse(crudService.patchUpdate(domain.getId(), domain, propChanged));
  }

  @PutMapping("/patch/multi")
  @Authenticated(access = AccessPermission.UPDATE)
  public ResponseEntity<?> patchMulti(@RequestBody @Validated CD dto, @RequestParam List<String> propChanged)
    throws BaseException {
    T domain = crudMapper.toDomain(dto);
    return RestUtil.successResponse(crudService.patchUpdate(domain.getId(), domain, propChanged));
  }

  @PutMapping("/update")
  @Authenticated(access = AccessPermission.UPDATE)
  public ResponseEntity<?> update(@RequestBody @Validated CD dto) throws BaseException {
    T domain = crudMapper.toDomain(dto);
    return RestUtil.successResponse(crudService.update(domain.getId(), domain));
  }

  @PostMapping
  @Authenticated(access = AccessPermission.CREATE)
  public ResponseEntity<RestResponse<CD>> add(@RequestBody @Validated CD dto)
    throws BaseException {
    return RestUtil.successResponse(crudMapper.toDto(crudService.create(crudMapper.toDomain(dto))));
  }

  @PostMapping("/delete")
  @Authenticated(access = AccessPermission.DELETE)
  public ResponseEntity<?> delete(@RequestBody List<String> ids) throws EntityNotFoundException {
    crudService.delete(ids);
    return RestUtil.successResponse(null);
  }

}
