package com.sixsprints.core.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.sixsprints.core.auth.BasicAuth;
import com.sixsprints.core.auth.BasicPermissionEnum;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.exception.EntityAlreadyExistsException;
import com.sixsprints.core.exception.EntityInvalidException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.mapper.GenericCrudMapper;
import com.sixsprints.core.service.GenericCrudService;
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

  private GenericCrudMapper<T, CD> crudMapper;

  private GenericCrudMapper<T, DD> detailMapper;

  public AbstractCrudController(GenericCrudService<T> crudService,
      GenericCrudMapper<T, SD> searchMapper, GenericCrudMapper<T, DD> detailMapper,
      GenericCrudMapper<T, CD> crudMapper) {
    super(crudService, searchMapper, detailMapper);
    this.crudService = crudService;
    this.crudMapper = crudMapper;
    this.detailMapper = detailMapper;
  }

  @PutMapping
  @BasicAuth(permission = BasicPermissionEnum.UPDATE)
  public ResponseEntity<RestResponse<DD>> patchUpdateOneBySlug(@RequestBody @Validated CD dto,
      @RequestParam String slug, @RequestParam String propChanged)
      throws EntityNotFoundException, EntityInvalidException {
    T domain = crudMapper.toDomain(dto);
    return RestUtil.successResponse(
        detailMapper.toDto(crudService.patchUpdateOneBySlug(slug, domain, propChanged)));
  }

  @PutMapping("/patch/multi")
  @BasicAuth(permission = BasicPermissionEnum.UPDATE)
  public ResponseEntity<RestResponse<DD>> patchUpdateOneBySlugMultiProps(
      @RequestBody @Validated CD dto, @RequestParam String slug,
      @RequestParam List<String> propsChanged)
      throws EntityNotFoundException, EntityInvalidException {
    T domain = crudMapper.toDomain(dto);
    return RestUtil.successResponse(
        detailMapper.toDto(crudService.patchUpdateOneBySlug(slug, domain, propsChanged)));
  }

  @PostMapping
  @BasicAuth(permission = BasicPermissionEnum.CREATE)
  public ResponseEntity<RestResponse<DD>> create(@RequestBody @Validated CD dto)
      throws EntityInvalidException, EntityAlreadyExistsException {
    return RestUtil.successResponse(
        detailMapper.toDto(crudService.insertOne(crudMapper.toDomain(dto))), HttpStatus.CREATED);
  }

  @PutMapping("/upsert")
  @BasicAuth(permission = BasicPermissionEnum.UPDATE)
  public ResponseEntity<RestResponse<DD>> upsert(@RequestBody @Validated CD dto)
      throws EntityInvalidException {
    return RestUtil
        .successResponse(detailMapper.toDto(crudService.upsertOne(crudMapper.toDomain(dto))));
  }

  @PostMapping("/delete/slug")
  @BasicAuth(permission = BasicPermissionEnum.DELETE)
  public ResponseEntity<?> deleteOneBySlug(@RequestParam String slug) {
    return RestUtil.successResponse(crudService.deleteOneBySlug(slug));
  }

  @PostMapping("/delete-bulk/slug")
  @BasicAuth(permission = BasicPermissionEnum.DELETE)
  public ResponseEntity<?> bulkDeleteBySlug(@RequestParam List<String> slugs) {
    return RestUtil.successResponse(crudService.bulkDeleteBySlug(slugs));
  }

}
