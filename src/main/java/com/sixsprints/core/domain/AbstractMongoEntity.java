package com.sixsprints.core.domain;

import java.io.Serializable;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@Document
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@FieldNameConstants
public class AbstractMongoEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  @Indexed(unique = true, sparse = true)
  private String slug;

  @Indexed(unique = true, sparse = true)
  protected Long sequence;

  @CreatedDate
  private Long dateCreated;

  @LastModifiedDate
  private Long dateModified;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String lastModifiedBy;

  public void copyEntityFrom(AbstractMongoEntity source) {
    this.id = source.id;
    this.dateCreated = source.dateCreated;
    this.dateModified = source.dateModified;
    this.slug = source.slug;
    this.createdBy = source.createdBy;
    this.lastModifiedBy = source.lastModifiedBy;
  }

}
