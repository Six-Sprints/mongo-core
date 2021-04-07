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

import com.sixsprints.core.annotation.Authenticated;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.dto.FilterRequestDto;
import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.enums.AccessPermission;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.core.transformer.GenericMapper;
import com.sixsprints.core.utils.RestResponse;
import com.sixsprints.core.utils.RestUtil;

public abstract class AbstractReadController<T extends AbstractMongoEntity, DTO> {

  private GenericCrudService<T> service;

  private GenericMapper<T, DTO> mapper;

  public AbstractReadController(GenericCrudService<T> service, GenericMapper<T, DTO> mapper) {
    this.service = service;
    this.mapper = mapper;
  }

  @GetMapping("/all/fields")
  @Authenticated(access = AccessPermission.READ)
  public ResponseEntity<RestResponse<List<FieldDto>>> fields(Locale locale) {
    return RestUtil.successResponse(localise(filterFields()));
  }

  @GetMapping("/{slug}")
  @Authenticated(access = AccessPermission.READ)
  public ResponseEntity<RestResponse<DTO>> findBySlug(@PathVariable String slug)
    throws EntityNotFoundException {
    return RestUtil.successResponse(mapper.toDto(service.findBySlug(slug)));
  }

  @PostMapping("/search")
  @Authenticated(access = AccessPermission.READ)
  public ResponseEntity<RestResponse<PageDto<DTO>>> filter(@RequestBody FilterRequestDto filterRequestDto) {
    return RestUtil.successResponse(mapper.pageEntityToPageDtoDto(service.filter(filterRequestDto)));
  }

  @PostMapping("/column/master")
  @Authenticated(access = AccessPermission.READ)
  public ResponseEntity<RestResponse<List<?>>> getDistinctValues(@RequestParam String column,
    @RequestBody FilterRequestDto filterRequestDto) {
    return RestUtil.successResponse(service.distinctColumnValues(column, filterRequestDto));
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

  protected ArrayList<FieldDto> filterFields() {
    return new ArrayList<>();
  }

}
