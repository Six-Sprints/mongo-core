package com.sixsprints.core.converters;

import java.time.LocalTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class IntegerToLocalTimeConverter implements Converter<Integer, LocalTime> {

  @Override
  public LocalTime convert(Integer source) {
    boolean hrMax = (source / 10000) > LocalTime.MAX.getHour();
    boolean minMax = hrMax || ((source / 100) % 100) > LocalTime.MAX.getMinute();
    boolean secMax = minMax || (source % 100) > LocalTime.MAX.getSecond();

    int hr = hrMax ? LocalTime.MAX.getHour() : (source / 10000);
    int min = minMax ? LocalTime.MAX.getMinute() : ((source / 100) % 100);
    int sec = secMax ? LocalTime.MAX.getSecond() : (source % 100);
    return LocalTime.of(hr, min, sec);
  }

}
