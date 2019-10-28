package com.sixsprints.core.utils.csv;

import java.util.Date;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

import com.sixsprints.core.utils.DateUtil;

public class ParseDateExport extends CellProcessorAdaptor {

  private boolean fullDate;

  public ParseDateExport(CellProcessor next) {
    super(next);
  }

  public ParseDateExport() {
    super();
  }

  public ParseDateExport(boolean fullDate) {
    super();
    this.fullDate = fullDate;
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

    }
    return next.execute(null, context);
  }

}