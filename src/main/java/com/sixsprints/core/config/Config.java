package com.sixsprints.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sixsprints.core.service.MessageSourceService;

@Configuration
public class Config implements WebMvcConfigurer {

  @Autowired
  private MessageSourceService messageSourceService;

  @Bean
  public LocalValidatorFactoryBean validator() {
    LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
    validatorFactoryBean.setValidationMessageSource(messageSourceService.messageSource());
    return validatorFactoryBean;
  }

  @Override
  public Validator getValidator() {
    return validator();
  }

}
