package com.sixsprints.core.utils.csv;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import com.sixsprints.core.utils.DateUtil;
import com.sixsprints.core.utils.DateUtil.DateUtilBuilder;

public class ParseDateExport extends CellProcessorAdaptor {

  public static final boolean THROW_EXCEPTION = true;

  public static final boolean IGNORE_EXCEPTION = false;

  private static final String MESSAGE = "Date is invalid";

  private boolean throwException;

  private String pattern;

  public ParseDateExport() {
    super();
  }

  public ParseDateExport(CellProcessor next) {
    super(next);
  }

  public ParseDateExport(boolean throwException) {
    super();
    this.throwException = throwException;
  }

  public ParseDateExport(String pattern, boolean throwException) {
    super();
    this.pattern = pattern;
    this.throwException = throwException;
  }

  public ParseDateExport(String pattern, boolean throwException, CellProcessor next) {
    super(next);
    this.pattern = pattern;
    this.throwException = throwException;
  }

  @Override
  public <T> T execute(Object value, CsvContext context) {
    if (value == null) {
      return next.execute(null, context);
    }
    try {
      DateUtilBuilder instance = DateUtil.instance();
      if (StringUtils.isNotBlank(pattern)) {
        instance = instance.datePattern(pattern);
      }
      return next.execute(instance.build().dateToString((Date) value), context);
    } catch (Exception ex) {
      if (throwException) {
        throw new SuperCsvCellProcessorException(MESSAGE, context, this);
      }
    }
    return next.execute(null, context);
  }

}