package com.sixsprints.core.utils.csv;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseBoolean extends CellProcessorAdaptor implements StringCellProcessor {

  private static final String MESSAGE = "Boolean value is invalid";

  private Boolean defaultValue;

  public ParseBoolean() {
    super();
  }

  public ParseBoolean(Boolean defaultValue) {
    super();
    this.defaultValue = defaultValue;
  }

  public ParseBoolean(Boolean defaultValue, CellProcessor next) {
    super(next);
    this.defaultValue = defaultValue;
  }

  @Override
  public <T> T execute(Object value, CsvContext context) {
    Boolean val = null;
    if (value != null) {
      val = parseBoolean(value.toString());
    }
    if (val == null) {
      throw new SuperCsvCellProcessorException(MESSAGE, context, this);
    }
    return next.execute(val, context);
  }

  private Boolean parseBoolean(String strVal) {
    Boolean val = null;
    if ("false".equalsIgnoreCase(strVal) || "0".equals(strVal) || "no".equalsIgnoreCase(strVal)) {
      val = false;
    } else if ("true".equalsIgnoreCase(strVal) || "1".equals(strVal) || "yes".equalsIgnoreCase(strVal)) {
      val = true;
    } else {
      val = defaultValue;
    }
    return val;
  }
}
