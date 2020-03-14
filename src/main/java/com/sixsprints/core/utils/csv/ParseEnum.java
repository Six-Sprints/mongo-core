package com.sixsprints.core.utils.csv;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

public class ParseEnum extends CellProcessorAdaptor {

  private static final String MESSAGE = "Invalid Enum";

  private final Class<? extends Enum<?>> enumClass;

  private final Object defaultValue;

  public ParseEnum(Class<? extends Enum<?>> enumClass, Object defaultValue) {
    super();
    this.enumClass = enumClass;
    this.defaultValue = defaultValue;
  }

  public ParseEnum(Class<? extends Enum<?>> enumClass, Object defaultValue, CellProcessor next) {
    super(next);
    this.enumClass = enumClass;
    this.defaultValue = defaultValue;
  }

  @Override
  public <T> T execute(Object value, CsvContext context) {
    if (value != null) {
      final String inputString = value.toString();
      Enum<?> enumConstant = findEnum(inputString);
      if (enumConstant != null) {
        return next.execute(enumConstant, context);
      }
      if (defaultValue == null) {
        throw new SuperCsvCellProcessorException(MESSAGE, context, this);
      }
    }
    return next.execute(findEnum(this.defaultValue), context);
  }

  private Enum<?> findEnum(Object value) {
    if (value == null) {
      return null;
    }
    for (final Enum<?> enumConstant : enumClass.getEnumConstants()) {
      String constantName = enumConstant.name();
      if (constantName.equalsIgnoreCase(value.toString())) {
        return enumConstant;
      }
    }
    return null;
  }
}