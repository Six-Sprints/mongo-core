package com.sixsprints.core.dto.filter;

import java.util.List;

import com.sixsprints.core.utils.AppConstants;

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
public class SetColumnFilter extends ColumnFilter {

  private List<?> values;

  @Builder.Default
  private String type = AppConstants.EQUALS;

}