package com.sixsprints.core.mock.domain.inheritance;

import com.sixsprints.core.domain.AbstractMongoEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public abstract class Animal extends AbstractMongoEntity {

  private static final long serialVersionUID = 1L;

  private Boolean canFly;

  private String name;

  private Long customId;

  private int count;

}
