package com.sixsprints.core.mock.domain;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.mock.domain.embedded.Address;
import com.sixsprints.core.mock.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Document
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractMongoEntity {

  private static final long serialVersionUID = -4856652993808911710L;

  @Indexed(unique = true)
  private String email;

  private String name;

  private Boolean flag;

  private Address address;

  @Indexed
  private String roleSlug;

  private Gender gender;

  private Long customId;

  private List<String> tags;

}
