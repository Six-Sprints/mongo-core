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

  private String prefix;

  private String collection;

  private Class<T> classType;

  private Sort defaultSort;

  private List<FieldDto> fields;

  @Builder.Default
  private boolean ignoreNullWhileBulkUpdate = Boolean.TRUE;

}
