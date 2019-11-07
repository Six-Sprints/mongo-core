package com.sixsprints.core.utils;

import java.util.List;

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

  public static <T extends AbstractMongoEntity> CellProcessor[] importProcessors(List<FieldDto> fields,
    MongoOperations mongo) {
    int total = fields.size();
    final CellProcessor[] processors = new CellProcessor[total];
    int i = 0;
    for (FieldDto field : fields) {
      processors[i++] = addImportProcessor(field, mongo);
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
      return new ParseEnum(field.getEnumClass(), (Enum<?>) field.getDefaultValue());
    }
    return null;
  }

  public static CellProcessor[] exportProcessors(List<FieldDto> fields) {
    int total = fields.size();
    final CellProcessor[] processors = new CellProcessor[total];
    int i = 0;
    for (FieldDto field : fields) {
      processors[i] = null;
      if (DataType.DATE.equals(field.getDataType())) {
        processors[i] = new ParseDateExport(true, ParseDateExport.IGNORE_EXCEPTION);
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
