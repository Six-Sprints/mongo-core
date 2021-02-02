
package com.sixsprints.core.transformer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.sixsprints.core.dto.PageDto;
import com.sixsprints.core.utils.DateUtil;

public abstract class GenericMapper<ENTITY, DTO> {

  @Autowired
  protected DateUtil dateUtil;

  public abstract DTO toDto(ENTITY entity);

  public abstract ENTITY toDomain(DTO dto);

  public List<DTO> toDto(List<ENTITY> entities) {
    List<DTO> dtos = new ArrayList<>();
    for (ENTITY entity : entities) {
      DTO dto = toDto(entity);
      if (dto != null) {
        dtos.add(dto);
      }
    }
    return dtos;
  }

  public List<ENTITY> toDomain(List<DTO> dtos) {
    List<ENTITY> entities = new ArrayList<>();
    for (DTO dto : dtos) {
      ENTITY entity = toDomain(dto);
      if (entity != null) {
        entities.add(entity);
      }
    }
    return entities;
  }

  public PageDto<DTO> pageEntityToPageDtoDto(Page<ENTITY> page) {
    PageDto<DTO> pageDto = pageAnyToPageDtoDto(page);
    pageDto.setContent(toDto(page.getContent()));
    return pageDto;
  }

  public PageDto<DTO> pageDtoToPageDtoDto(Page<DTO> page) {
    PageDto<DTO> pageDto = pageAnyToPageDtoDto(page);
    pageDto.setContent(page.getContent());
    return pageDto;
  }

  public PageDto<ENTITY> pageEntityToPageDtoEntity(Page<ENTITY> page) {
    PageDto<ENTITY> pageDto = new PageDto<>();
    pageDto.setCurrentPageSize(page.getNumberOfElements());
    pageDto.setCurrentPage(page.getNumber());
    pageDto.setTotalElements(page.getTotalElements());
    pageDto.setTotalPages(page.getTotalPages());
    pageDto.setContent(page.getContent());
    return pageDto;
  }

  private PageDto<DTO> pageAnyToPageDtoDto(Page<?> page) {
    PageDto<DTO> pageDto = new PageDto<>();
    pageDto.setCurrentPageSize(page.getNumberOfElements());
    pageDto.setCurrentPage(page.getNumber());
    pageDto.setTotalElements(page.getTotalElements());
    pageDto.setTotalPages(page.getTotalPages());
    return pageDto;
  }

  protected String epochToString(Long epoch) {
    return epoch == null ? null
      : dateUtil.dateToString(dateUtil.initDateFromLong(epoch).toDate());
  }

  protected Date epochToDate(Long epoch) {
    return epoch == null ? null : dateUtil.initDateFromLong(epoch).toDate();
  }

  protected Long dateToEpoch(Date date) {
    return date == null ? null : dateUtil.initDateFromDate(date).getMillis();
  }

  protected Long dateStringToEpoch(String date) {
    return date == null ? null : dateUtil.stringToDate(date).getMillis();
  }

}
