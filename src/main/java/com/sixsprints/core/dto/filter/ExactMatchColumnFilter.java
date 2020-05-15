package com.sixsprints.core.dto.filter;

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
public class ExactMatchColumnFilter extends ColumnFilter {

  private String filter;

  @Builder.Default
  private String type = AppConstants.EQUALS;

}