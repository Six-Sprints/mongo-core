package com.sixsprints.core.utils;

import java.util.Locale;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MessageSourceHolder {

  private final MessageSource injectedMessageSource;

  private static MessageSource messageSource;

  @jakarta.annotation.PostConstruct
  public void init() {
    messageSource = injectedMessageSource;
  }

  public static String resolve(String key, Object[] args, Locale locale) {
    if (key == null) {
      return null;
    }
    if (messageSource == null) {
      return fallback(key, args);
    }
    try {
      return messageSource.getMessage(key, args, key, locale);
    } catch (Exception e) {
      return fallback(key, args);
    }
  }

  public static String resolve(String key, Object[] args) {
    return resolve(key, args, LocaleContextHolder.getLocale());
  }

  public static String resolve(String key, List<Object> args, Locale locale) {
    Object[] arr = args == null ? null : args.toArray();
    return resolve(key, arr, locale);
  }

  public static String resolve(String key, List<Object> args) {
    Object[] arr = args == null ? null : args.toArray();
    return resolve(key, arr, LocaleContextHolder.getLocale());
  }

  public static String resolve(String key, Locale locale) {
    return resolve(key, (Object[]) null, locale);
  }

  public static String resolve(String key) {
    return resolve(key, (Object[]) null, LocaleContextHolder.getLocale());
  }

  private static String fallback(String key, Object[] args) {
    if (args == null || args.length == 0) {
      return key;
    }
    try {
      return String.format(key, args);
    } catch (Exception ex) {
      return key;
    }
  }
}
