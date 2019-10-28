package com.sixsprints.core.utils;

import java.util.List;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.enums.DataType;
import com.sixsprints.core.utils.csv.ParseDateExport;
import com.sixsprints.core.utils.csv.ParseDateImport;

public class CellProcessorUtil {

  public static CellProcessor[] importProcessors(List<FieldDto> fields) {
    int total = fields.size();
    final CellProcessor[] processors = new CellProcessor[total];
    int i = 0;
    for (FieldDto field : fields) {
      processors[i] = null;
      if (DataType.DATE.equals(field.getDataType())) {
        processors[i] = new ParseDateImport();
      }
      i++;
    }
    return processors;
  }

  public static CellProcessor[] exportProcessors(List<FieldDto> fields) {
    int total = fields.size();
    final CellProcessor[] processors = new CellProcessor[total];
    int i = 0;
    for (FieldDto field : fields) {
      processors[i] = null;
      if (DataType.DATE.equals(field.getDataType())) {
        processors[i] = new ParseDateExport(true);
      }
      i++;
    }
    return processors;
  }

  public static String toExcelCellNotation(int row, int number) {
    StringBuilder sb = new StringBuilder();
    while (number-- > 0) {
      sb.append((char) ('A' + (number % 26)));
      number /= 26;
    }
    return sb.reverse().toString() + row;
  }

}
