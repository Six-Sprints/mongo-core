package com.sixsprints.core.utils.csv;

import java.util.Date;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import com.sixsprints.core.utils.DateUtil;

public class ParseDateExport extends CellProcessorAdaptor {

  public static final boolean THROW_EXCEPTION = true;

  public static final boolean IGNORE_EXCEPTION = false;

  private static final String MESSAGE = "Date is invalid";

  private boolean fullDate;

  private boolean throwException;

  public ParseDateExport(CellProcessor next) {
    super(next);
  }

  public ParseDateExport() {
    super();
  }

  public ParseDateExport(boolean fullDate, boolean throwException) {
    super();
    this.fullDate = fullDate;
    this.throwException = throwException;
  }

  @Override
  public <T> T execute(Object value, CsvContext context) {
    if (value == null) {
      return next.execute(null, context);
    }
    try {
      if (fullDate) {
        return next.execute(DateUtil.formatToDateTime((Date) value), context);
      }
      return next.execute(DateUtil.format((Date) value), context);
    } catch (Exception ex) {
      if (throwException) {
        throw new SuperCsvCellProcessorException(MESSAGE, context, this);
      }
    }
    return next.execute(null, context);
  }

}