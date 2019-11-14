package com.sixsprints.core.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import com.sixsprints.core.exception.BaseRuntimeException;

@ReadingConverter
public class StringToClassConverter implements Converter<String, Class<?>> {

  @Override
  public Class<?> convert(String source) {
    try {
      Class<?> forName = Class.forName(source);
      return forName;
    } catch (Exception e) {
      throw BaseRuntimeException.builder().error("Unable to convert {0} to desired class type.")
        .arguments(new Object[] { source }).build();
    }
  }

}
