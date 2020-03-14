package com.sixsprints.core.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class ClassToStringConverter implements Converter<Class<?>, String> {

  @Override
  public String convert(Class<?> source) {
    String name = source.getName();
    return name;
  }

}
