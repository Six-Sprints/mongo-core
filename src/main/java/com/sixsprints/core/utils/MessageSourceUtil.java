package com.sixsprints.core.utils;

import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import com.sixsprints.core.service.MessageSourceService;

public class MessageSourceUtil {

  private MessageSourceUtil() {
  }

  public static String resolveMessage(MessageSourceService messageSourceService, String key, List<Object> args,
    Locale locale) {
    try {
      Object[] arg = null;
      if (!CollectionUtils.isEmpty(args)) {
        arg = args.toArray();
      }
      String errorMessage = messageSourceService.messageSource().getMessage(key, arg, locale);
      return errorMessage;
    } catch (Exception ex) {
      return StringUtils.hasText(key) ? key : ex.getMessage();
    }
  }

}
