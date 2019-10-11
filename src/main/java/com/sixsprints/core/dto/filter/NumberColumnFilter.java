package com.sixsprints.core.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NumberColumnFilter extends ColumnFilter {

  private String type;

  private Integer filter;

  private Integer filterTo;

  private ConditionDto condition1;

  private ConditionDto condition2;

  private String operator;

}