package com.sixsprints.core.converters;

import java.math.BigDecimal;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import com.mongodb.lang.NonNull;
import com.sixsprints.core.exception.BaseRuntimeException;

@ReadingConverter
public class Decimal128ToBigDecimalConverter implements Converter<Decimal128, BigDecimal> {

  @Override
  public BigDecimal convert(@NonNull Decimal128 source) {
    try {
      return source.bigDecimalValue();
    } catch (Exception e) {
      throw BaseRuntimeException.builder().error("Unable to convert {0} to desired class type.")
        .argument(source).build();
    }
  }

}
