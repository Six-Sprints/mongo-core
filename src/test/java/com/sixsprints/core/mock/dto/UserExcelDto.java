package com.sixsprints.core.mock.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.sixsprints.core.dto.GenericExcelImportDto;
import com.sixsprints.core.mock.domain.embedded.Address;
import com.sixsprints.core.mock.enums.Gender;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserExcelDto extends GenericExcelImportDto {

  private static final long serialVersionUID = 1L;

  @Email
  @NotBlank
  @Excel(name = "Email", fixedIndex = 2)
  private String email;

  @Excel(name = "Name", fixedIndex = 3)
  private String name;

  @Excel(name = "Flag", fixedIndex = 4)
  private Boolean flag;

  private Address address;

  @Excel(name = "Date Created", fixedIndex = 5)
  protected String dateCreated;

  @Excel(name = "Role ID", fixedIndex = 6)
  private String roleSlug;

  @Excel(name = "Gender", fixedIndex = 7)
  private Gender gender;

  @Excel(name = "Custom ID", fixedIndex = 8)
  private Long customId;

}
