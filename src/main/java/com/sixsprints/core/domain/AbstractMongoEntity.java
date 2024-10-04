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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Document
@Data
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AbstractMongoEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  public static final String ID = "id";

  public static final String SLUG = "slug";

  public static final String DATE_MODIFIED = "dateModified";

  public static final String LAST_MODIFIED_BY = "lastModifiedBy";

  @Id
  protected String id;

  @Indexed(unique = true, sparse = true)
  protected String slug;

  @Indexed(unique = true, sparse = true)
  protected Integer sequence;

  @Indexed
  protected Long sequence;

  @Indexed
  @Builder.Default
  protected Boolean active = Boolean.TRUE;

  @CreatedDate
  protected Long dateCreated;

  @LastModifiedDate
  protected Long dateModified;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String lastModifiedBy;

  public void copyEntityFrom(AbstractMongoEntity source) {
    this.id = source.id;
    this.active = source.active;
    this.dateCreated = source.dateCreated;
    this.dateModified = source.dateModified;
    this.slug = source.slug;
    this.createdBy = source.createdBy;
    this.lastModifiedBy = source.lastModifiedBy;
  }

}