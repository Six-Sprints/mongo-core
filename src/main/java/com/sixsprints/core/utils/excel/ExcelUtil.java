package com.sixsprints.core.utils.excel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.result.ExcelImportResult;

public class ExcelUtil {

  public static <DTO> List<DTO> importData(InputStream inputStream, ImportParams params, Class<DTO> classType)
    throws Exception {

    ExcelImportResult<DTO> result = ExcelImportUtil.<DTO>importExcelMore(inputStream, classType, params);
    List<DTO> list = result.getList();
    if (CollectionUtils.isEmpty(list)) {
      return new ArrayList<>();
    }
    return list;

  }

}
