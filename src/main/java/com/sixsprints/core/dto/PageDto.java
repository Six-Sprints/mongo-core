
package com.sixsprints.core.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T> implements Serializable {

  private static final long serialVersionUID = 4762156998065006126L;

  private Integer currentPage;

  private Integer currentPageSize;

  private Integer totalPages;

  private Long totalElements;

  @Builder.Default
  private List<T> content = new ArrayList<T>();

}
