package com.sixsprints.core.config;

import java.time.LocalTime;

import org.joda.time.DateTimeZone;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.sixsprints.core.converters.LocalTimeSerializerAsString;
import com.sixsprints.core.utils.DateUtil;

@Configuration
public class ParentBeans {

  @Bean
  public DateUtil dateUtil() {
    return DateUtil.instance().timeZone(defaultTimeZone())
      .datePattern(defaultDateFormat()).shortDatePattern(defaultShortDateFormat()).build();
  }

  @Bean
  public ObjectMapper mapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setTimeZone(defaultTimeZone().toTimeZone());
    SimpleModule module = module();
    mapper.registerModule(module);
    return mapper;
  }

  protected SimpleModule module() {
    SimpleModule module = new SimpleModule();
    module.addSerializer(LocalTime.class, LocalTimeSerializerAsString.INSTANCE);
    module.addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
    return module;
  }

  protected DateTimeZone defaultTimeZone() {
    return DateUtil.DEFAULT_TIMEZONE;
  }

  protected String defaultDateFormat() {
    return DateUtil.DEFAULT_DATE_PATTERN;
  }

  protected String defaultShortDateFormat() {
    return DateUtil.DEFAULT_SHORT_DATE_PATTERN;
  }

}
