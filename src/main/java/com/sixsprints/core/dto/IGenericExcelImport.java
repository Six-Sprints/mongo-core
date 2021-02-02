package com.sixsprints.core.dto;

import com.sixsprints.core.enums.ImportOperation;

public interface IGenericExcelImport {

  Long getSerialNo();

  ImportOperation getOperation();

}
