package com.sixsprints.core.converters;

import java.time.LocalTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class LocalTimeToIntegerConverter implements Converter<LocalTime, Integer> {

  @Override
  public Integer convert(LocalTime source) {
    return (source.getHour() * 10000) + (source.getMinute() * 100) + (source.getSecond());
  }

}
