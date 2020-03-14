package com.sixsprints.core.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportLogDetails {

  private String entity;

  private List<UploadError> errors;

  private List<String> unknownErrors;

  private Integer totalRowCount;

  private Integer successRowCount;

  private Integer warningRowCount;

  private Integer errorRowCount;

}