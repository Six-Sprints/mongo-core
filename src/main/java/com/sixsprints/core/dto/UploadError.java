package com.sixsprints.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UploadError implements Comparable<UploadError> {

  private Integer row;

  private Integer col;

  private String cellLocation;

  private String message;

  private String type;

  @Override
  public int compareTo(UploadError other) {
    int order = this.row - other.row;
    if (order == 0) {
      return this.col - other.col;
    }
    return order;
  }

}