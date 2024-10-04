package com.sixsprints.core.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.sixsprints.core.auth.BasicAuth;
import com.sixsprints.core.auth.BasicPermissionEnum;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.generic.read.GenericReadService;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.core.transformer.GenericMapper;
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

  private GenericMapper<T, SD> searchDtoMapper;

  private GenericMapper<T, DD> detailDtoMapper;

  public AbstractReadController(GenericCrudService<T> service, GenericMapper<T, SD> searchDtoMapper,
    GenericMapper<T, DD> detailDtoMapper) {
    this.readService = service;
    this.searchDtoMapper = searchDtoMapper;
    this.detailDtoMapper = detailDtoMapper;
  }

  @GetMapping("/all/fields")
  @BasicAuth(permission = BasicPermissionEnum.VIEW)
  public ResponseEntity<RestResponse<List<FieldDto>>> fields() {
    return RestUtil.successResponse(localise(searchDtoFields()));
  }

  @GetMapping("/{slug}")
  @BasicAuth(permission = BasicPermissionEnum.VIEW)
  public ResponseEntity<RestResponse<DD>> findBySlug(@PathVariable String slug)
    throws EntityNotFoundException {
    return RestUtil.successResponse(detailDtoMapper.toDto(readService.findBySlug(slug)));
  }

  @PostMapping("/search")
  @BasicAuth(permission = BasicPermissionEnum.VIEW)
  public ResponseEntity<RestResponse<PageDto<SD>>> filter(@RequestBody FilterRequestDto filterRequestDto) {
    return RestUtil.successResponse(searchDtoMapper.pageEntityToPageDtoDto(readService.filter(filterRequestDto)));
  }

  @PostMapping("/column/master")
  @BasicAuth(permission = BasicPermissionEnum.VIEW)
  public ResponseEntity<RestResponse<List<?>>> getDistinctValues(@RequestParam String column,
    @RequestBody FilterRequestDto filterRequestDto) {
    return RestUtil.successResponse(readService.distinctColumnValues(column, filterRequestDto));
  }

  protected List<FieldDto> localise(List<FieldDto> fields) {

    Locale locale = LocaleContextHolder.getLocale();
    if (fields != null && !fields.isEmpty()) {
      for (FieldDto dto : fields) {
        String displayName = dto.getLocalizedDisplay().get(locale);
        if (StringUtils.isNotBlank(displayName)) {
          dto.setDisplayName(displayName);
        } else {
          dto.setDisplayName(dto.getLocalizedDisplay().get(Locale.ENGLISH));
        }
        dto.setLocalizedDisplay(null);
      }
    }
    return fields;
  }

  protected List<FieldDto> searchDtoFields() {
    return new ArrayList<>();
  }

}
