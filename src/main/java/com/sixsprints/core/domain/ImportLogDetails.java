package com.sixsprints.core.domain;

import java.util.List;

import com.sixsprints.core.dto.UploadError;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImportLogDetails extends AbstractMongoEntity {

  private static final long serialVersionUID = 1L;

  private String entity;

  private List<UploadError> errors;

  private List<String> unknownErrors;

  private Integer totalRowCount;

  private Integer successRowCount;

  private Integer warningRowCount;

  private Integer errorRowCount;

}