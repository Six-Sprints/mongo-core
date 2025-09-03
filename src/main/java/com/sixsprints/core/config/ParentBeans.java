package com.sixsprints.core.config;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.sixsprints.core.converters.LocalTimeSerializerAsString;
import com.sixsprints.core.utils.DateUtil;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Configuration
public class ParentBeans {

  @Bean
  protected DateUtil dateUtil() {
    return DateUtil.instance().timeZone(defaultTimeZone())
      .datePattern(defaultDateFormat()).shortDatePattern(defaultShortDateFormat()).build();
  }

  @Bean
  protected ObjectMapper mapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setTimeZone(TimeZone.getTimeZone(defaultTimeZone()));
    SimpleModule module = module();
    mapper.registerModule(module);
    mapper.registerModule(new JavaTimeModule());
    return mapper;
  }

  @Bean
  protected Validator validator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    return validator;
  }

  protected SimpleModule module() {
    SimpleModule module = new SimpleModule();
    module.addSerializer(LocalTime.class, LocalTimeSerializerAsString.INSTANCE);
    module.addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
    return module;
  }

  protected ZoneId defaultTimeZone() {
    return DateUtil.DEFAULT_TIMEZONE;
  }

  protected String defaultDateFormat() {
    return DateUtil.DEFAULT_DATE_PATTERN;
  }

  protected String defaultShortDateFormat() {
    return DateUtil.DEFAULT_SHORT_DATE_PATTERN;
  }

}
