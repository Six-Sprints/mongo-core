package com.sixsprints.core.converters;

import java.time.LocalTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class IntegerToLocalTimeConverter implements Converter<Integer, LocalTime> {

  @Override
  public LocalTime convert(Integer source) {
    int hr = (source / 10000) % LocalTime.MAX.getHour();
    int min = ((source / 100) % 100) % LocalTime.MAX.getMinute();
    int sec = (source % 100) % LocalTime.MAX.getSecond();
    return LocalTime.of(hr, min, sec);
  }
}
