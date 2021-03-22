package com.sixsprints.core.dto;

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
public class GenericEntityDto {

  private String id;

  private String slug;

  private Long dateCreated;

  private Long dateModified;

  private String createdBy;

  private String lastModifiedBy;

}
