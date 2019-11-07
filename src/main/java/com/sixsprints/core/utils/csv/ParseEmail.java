package com.sixsprints.core.utils.csv;

import org.apache.commons.validator.routines.EmailValidator;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseEmail extends CellProcessorAdaptor implements StringCellProcessor {

  private static final String MESSAGE = "Email ID is invalid!";

  public ParseEmail() {
    super();
  }

  public ParseEmail(CellProcessor next) {
    super(next);
  }

  public Boolean validateEmailAddress(String emailAddress) {
    return EmailValidator.getInstance().isValid(emailAddress);
  }

  @Override
  public <T> T execute(Object value, CsvContext context) {
    if (value != null) {
      String strVal = value.toString();
      Boolean isValid = validateEmailAddress(strVal);
      if (isValid) {
        return next.execute(value, context);
      }
      throw new SuperCsvCellProcessorException(MESSAGE, context, this);
    }
    return next.execute(null, context);
  }
}