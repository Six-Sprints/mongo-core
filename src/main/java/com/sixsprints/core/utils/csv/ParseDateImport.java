package com.sixsprints.core.utils.csv;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

import com.sixsprints.core.utils.DateUtil;

public class ParseDateImport extends CellProcessorAdaptor {

  public ParseDateImport(CellProcessor next) {
    super(next);
  }

  public ParseDateImport() {
    super();
  }

  @Override
  public <T> T execute(Object value, CsvContext context) {
    if (value != null) {
      return next.execute(DateUtil.stringToDate(value.toString()), context);
    }
    return next.execute(null, context);
  }

}