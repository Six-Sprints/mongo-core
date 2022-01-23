package com.sixsprints.core.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "slug")
public class GenericEntityDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private String slug;

  private Long dateCreated;

  private Long dateModified;

  private String createdBy;

  private String lastModifiedBy;

}
