package com.sixsprints.core.utils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.sixsprints.core.dto.FieldDto;

public class FieldUtil {

  private FieldUtil() {
  }

  public static List<FieldDto> fields(List<FieldDto> fields, Locale locale) {
    return fields.stream().map(field -> {
      String displayName = field.getDisplayName();
      if (field.getLocalizedDisplay() != null && field.getLocalizedDisplay().containsKey(locale)) {
        displayName = field.getLocalizedDisplay().get(locale);
      }
      field.setDisplayName(displayName);
      return field;
    }).collect(Collectors.toList());
  }

}
