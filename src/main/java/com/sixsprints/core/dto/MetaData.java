package com.sixsprints.core.dto;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.sixsprints.core.domain.AbstractMongoEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaData<T extends AbstractMongoEntity> {

  private Class<T> classType;

  private Class<?> dtoClassType;

  private Sort defaultSort;

  private List<FieldDto> fields;

  private String entityName;

  @Builder.Default
  private boolean ignoreNullWhileBulkUpdate = Boolean.TRUE;

}
