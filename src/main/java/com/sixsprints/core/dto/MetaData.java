package com.sixsprints.core.dto;

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

}
