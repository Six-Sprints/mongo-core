package com.sixsprints.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.sixsprints.core.auth.BasicAuth;
import com.sixsprints.core.auth.BasicPermissionEnum;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.generic.read.GenericReadService;
import com.sixsprints.core.mapper.GenericCrudMapper;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;

/**
 *
 * @param <T>  - Domain Class Type
 * @param <SD> - Search DTO Class Type
 * @param <DD> - Detailed DTO Class Type
 */
public abstract class AbstractReadController<T extends AbstractMongoEntity, SD, DD> {

  private GenericReadService<T> readService;

  private GenericCrudMapper<T, SD> searchDtoMapper;

  private GenericCrudMapper<T, DD> detailDtoMapper;

  public AbstractReadController(GenericCrudService<T> service,
      GenericCrudMapper<T, SD> searchDtoMapper, GenericCrudMapper<T, DD> detailDtoMapper) {
    this.readService = service;
    this.searchDtoMapper = searchDtoMapper;
    this.detailDtoMapper = detailDtoMapper;
  }

  @GetMapping("/{slug}")
  @BasicAuth(permission = BasicPermissionEnum.READ)
  public ResponseEntity<RestResponse<DD>> findBySlug(@PathVariable String slug) {
    return RestUtil
        .successResponse(detailDtoMapper.toDto(readService.findOneBySlug(slug).orElse(null)));
  }

  @PostMapping("/search")
  @BasicAuth(permission = BasicPermissionEnum.READ)
  public ResponseEntity<RestResponse<PageDto<SD>>> filter(
      @RequestBody FilterRequestDto filterRequestDto) {
    return RestUtil.successResponse(searchDtoMapper
        .pageEntityToPageDtoDto(readService.filterByFilterRequestDto(filterRequestDto)));
  }

}
