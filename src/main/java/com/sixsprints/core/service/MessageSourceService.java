package com.sixsprints.core.service;

import java.util.Locale;

import org.springframework.context.MessageSource;

public interface MessageSourceService {

  MessageSource messageSource();

  String genericError();

  Locale defaultLocale();

}
