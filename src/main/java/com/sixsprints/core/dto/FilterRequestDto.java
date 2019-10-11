package com.sixsprints.core.dto;

import java.util.List;
import java.util.Map;

import com.sixsprints.core.dto.filter.ColumnFilter;
import com.sixsprints.core.dto.filter.SortModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequestDto {

  private int page;

  private int size;

  private Map<String, ColumnFilter> filterModel;

  private List<SortModel> sortModel;

  private Boolean deepFilter;

}
