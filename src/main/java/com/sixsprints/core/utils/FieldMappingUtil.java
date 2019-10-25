package com.sixsprints.core.utils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.exception.BaseException;

public class FieldMappingUtil {

  private static final String ILLEGAL_FILE_HEADERS = "illegal.file.headers";

  public static String[] createHeaders(String[] mappings, List<FieldDto> fields, Locale locale) {
    String[] headers = new String[mappings.length];
    int i = 0;
    for (String mapping : mappings) {
      headers[i++] = findFieldLocaleName(fields, mapping, locale);
    }
    return headers;
  }

  public static String[] genericMappings(int total, List<FieldDto> fields) throws BaseException {
    if (total != fields.size()) {
      throw BaseException.builder().error(ILLEGAL_FILE_HEADERS).build();
    }
    return genericMappings(fields);
  }

  public static String[] genericMappings(List<FieldDto> fields) {
    String[] fieldMappings = new String[fields.size()];
    int i = 0;
    for (FieldDto field : fields) {
      fieldMappings[i++] = field.getName();
    }
    return fieldMappings;
  }

  private static String findFieldLocaleName(List<FieldDto> fields, String mapping, Locale locale) {
    Optional<FieldDto> fieldDto = fields.stream().filter(field -> field.getName().equals(mapping)).findFirst();
    if (fieldDto.isPresent()) {
      return fieldDto.get().getLocalizedDisplay().get(locale);
    }
    return mapping;
  }

}
