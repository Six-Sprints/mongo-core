package com.sixsprints.core.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import com.sixsprints.core.exception.BaseRuntimeException;
import com.sixsprints.core.constants.ExceptionConstants;

@ReadingConverter
public class StringToClassConverter implements Converter<String, Class<?>> {

  @Override
  public Class<?> convert(String source) {
    try {
      Class<?> forName = Class.forName(source);
      return forName;
    } catch (Exception e) {
      throw BaseRuntimeException.builder().error(ExceptionConstants.UNABLE_TO_CONVERT)
          .argument(source).build();
    }
  }

}
