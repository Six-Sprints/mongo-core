package com.sixsprints.core.utils;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import cn.afterturn.easypoi.excel.export.styler.ExcelExportStylerDefaultImpl;

public class ExcelDefaultStyles extends ExcelExportStylerDefaultImpl {

  public ExcelDefaultStyles(Workbook workbook) {
    super(workbook);
  }

  @Override
  public CellStyle getTitleStyle(short color) {
    CellStyle cellStyle = workbook.createCellStyle();
    return cellStyle;
  }

  @Override
  public CellStyle getHeaderStyle(short color) {
    CellStyle cellStyle = workbook.createCellStyle();
    return cellStyle;
  }

  @Override
  public CellStyle stringSeptailStyle(Workbook workbook, boolean isWrap) {
    CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setWrapText(isWrap);
    return cellStyle;
  }

  @Override
  public CellStyle stringNoneStyle(Workbook workbook, boolean isWrap) {
    CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setWrapText(isWrap);
    return cellStyle;
  }

}
