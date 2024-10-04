package com.sixsprints.core.dto;

import java.io.Serializable;

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
public class UploadError implements Serializable {

  private static final long serialVersionUID = 1L;

  private String message;

  private String type;

  private String key;

}