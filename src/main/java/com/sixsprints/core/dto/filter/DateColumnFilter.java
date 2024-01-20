package com.sixsprints.core.dto.filter;

import java.util.Date;

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
public class DateColumnFilter extends ColumnFilter {

  private String type;

  private Date filter;

  private Date filterTo;

  @Builder.Default
  private boolean exactMatch = Boolean.FALSE;

}