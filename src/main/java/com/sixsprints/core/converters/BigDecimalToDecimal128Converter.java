package com.sixsprints.core.converters;

import java.math.BigDecimal;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import com.sixsprints.core.exception.BaseRuntimeException;

@WritingConverter
public class BigDecimalToDecimal128Converter implements Converter<BigDecimal, Decimal128> {

  @Override
  public Decimal128 convert(BigDecimal source) {
    try {
      return new Decimal128(source);
    } catch (Exception e) {
      throw BaseRuntimeException.builder().error("Unable to convert {0} to desired class type.")
        .argument(source).build();
    }
  }
}
