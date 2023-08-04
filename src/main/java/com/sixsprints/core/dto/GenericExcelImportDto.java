package com.sixsprints.core.dto;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;

import com.sixsprints.core.enums.ImportOperation;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GenericExcelImportDto implements IGenericExcelImport, Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull
  @Excel(name = "S.No.", fixedIndex = 0, orderNum = "-2")
  private Long serialNo;

  @NotNull
  @Excel(name = "Operation", fixedIndex = 1, replace = {
      "add/change_UPSERT", "Add/change_UPSERT", "Add/Change_UPSERT", "ADD/CHANGE_UPSERT",
      "Delete_DELETE", "delete_DELETE" }, orderNum = "-1")
  private ImportOperation operation;

}
