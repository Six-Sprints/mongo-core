package com.sixsprints.core.dto.filter;

import java.util.List;

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
public class SearchColumnFilter extends ColumnFilter {

  private String filter;

  private List<String> fields;
  
  private boolean slugExcludedFromSearch;

}