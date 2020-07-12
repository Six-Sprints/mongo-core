package com.sixsprints.core.mock.dto;

import java.util.Date;

import com.sixsprints.core.mock.domain.embedded.Address;
import com.sixsprints.core.mock.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

  private String email;

  private String name;

  private Boolean flag;

  private Address address;

  protected Date dateCreated;

  private String roleName;
  
  private Gender gender;
  
  private Long customId;

}
