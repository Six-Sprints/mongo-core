package com.sixsprints.core.dto;

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
@EqualsAndHashCode
public class ImportResponseWrapper<T> {

  private List<T> data;

  private ImportLogDetails importLogDetails;

  private String[] firstLine;

}