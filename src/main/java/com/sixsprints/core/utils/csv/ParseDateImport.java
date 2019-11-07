package com.sixsprints.core.utils.csv;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import com.sixsprints.core.utils.DateUtil;

public class ParseDateImport extends CellProcessorAdaptor {

  public static final boolean THROW_EXCEPTION = true;

  public static final boolean IGNORE_EXCEPTION = false;

  private boolean throwException;

  private static final String MESSAGE = "Date is invalid";

  public ParseDateImport(boolean throwException, CellProcessor next) {
    super(next);
    this.throwException = throwException;
  }

  public ParseDateImport(boolean throwException) {
    super();
    this.throwException = throwException;
  }

  @Override
  public <T> T execute(Object value, CsvContext context) {
    if (value != null) {
      try {
        return next.execute(DateUtil.stringToDate(value.toString()), context);
      } catch (Exception ex) {
        if (throwException) {
          throw new SuperCsvCellProcessorException(MESSAGE, context, this);
        }
      }
    }
    return next.execute(null, context);
  }

}