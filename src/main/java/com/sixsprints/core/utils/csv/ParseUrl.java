package com.sixsprints.core.utils.csv;

import org.apache.commons.validator.routines.UrlValidator;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseUrl extends CellProcessorAdaptor implements StringCellProcessor {

  private static final String MESSAGE = "URL is invalid!";

  public ParseUrl() {
    super();
  }

  public ParseUrl(CellProcessor next) {
    super(next);
  }

  public Boolean validateUrl(String url) {
    return UrlValidator.getInstance().isValid(url);
  }

  @Override
  public <T> T execute(Object value, CsvContext context) {
    if (value != null) {
      String strVal = value.toString();
      Boolean isValid = validateUrl(strVal);
      if (isValid) {
        return next.execute(value, context);
      }
      throw new SuperCsvCellProcessorException(MESSAGE, context, this);
    }
    return next.execute(null, context);
  }
}