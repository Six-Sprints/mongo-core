package com.sixsprints.core.utils;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoOperations;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.enums.DataType;
import com.sixsprints.core.utils.csv.ParseBoolean;
import com.sixsprints.core.utils.csv.ParseDateExport;
import com.sixsprints.core.utils.csv.ParseDateImport;
import com.sixsprints.core.utils.csv.ParseEmail;
import com.sixsprints.core.utils.csv.ParseEnum;
import com.sixsprints.core.utils.csv.ParseMasterDataValue;
import com.sixsprints.core.utils.csv.ParseUrl;

public class CellProcessorUtil {

  public static CellProcessor[] importProcessors(List<FieldDto> fields, Map<String, CellProcessor> map,
    MongoOperations mongo) {
    int total = fields.size();
    final CellProcessor[] processors = new CellProcessor[total];
    int i = 0;
    for (FieldDto field : fields) {
      if (map.containsKey(field.getName())) {
        processors[i++] = map.get(field.getName());
      } else if (map.containsKey(field.getDataType().name())) {
        processors[i++] = map.get(field.getDataType().name());
      } else {
        processors[i++] = addImportProcessor(field, mongo);
      }
    }
    return processors;
  }

  public static CellProcessor[] exportProcessors(List<FieldDto> fields,
    Map<String, CellProcessor> map) {
    int total = fields.size();
    final CellProcessor[] processors = new CellProcessor[total];
    int i = 0;
    for (FieldDto field : fields) {
      if (map.containsKey(field.getName())) {
        processors[i++] = map.get(field.getName());
      } else if (map.containsKey(field.getDataType().name())) {
        processors[i++] = map.get(field.getDataType().name());
      } else if (DataType.DATE.equals(field.getDataType())) {
        processors[i++] = new ParseDateExport(ParseDateExport.IGNORE_EXCEPTION);
      } else {
        processors[i++] = null;
      }
    }
    return processors;
  }

  private static <T extends AbstractMongoEntity> CellProcessor addImportProcessor(FieldDto field,
    MongoOperations mongo) {
    switch (field.getDataType()) {
    case AUTO_COMPLETE:
    case SELECT:
      return new ParseMasterDataValue(field.getCollectionName(), field.getColumnName(), mongo);
    case BOOLEAN:
      return new ParseBoolean();
    case DATE:
      return new ParseDateImport(ParseDateImport.IGNORE_EXCEPTION);
    case IMAGE:
      return new ParseUrl();
    case LINK:
      return new ParseUrl();
    case NUMBER:
      return new ParseDouble();
    case TEXT:
      break;
    case TEXT_AREA:
      break;
    case EMAIL:
      return new ParseEmail();
    case ENUM:
      return new ParseEnum(field.getEnumClass(), field.getDefaultValue());
    }
    return null;
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
