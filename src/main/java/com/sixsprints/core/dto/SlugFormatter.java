package com.sixsprints.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlugFormatter {

  private String prefix;

  private String collection;

  private Long minimumSequenceNumber;

}
