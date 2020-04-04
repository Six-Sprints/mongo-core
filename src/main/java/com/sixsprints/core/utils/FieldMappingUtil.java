package com.sixsprints.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.StringUtils;

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
    Optional<FieldDto> fieldDto = fields.stream().filter(field -> match(field.getName(), mapping)).findFirst();
    if (fieldDto.isPresent()) {
      if (fieldDto.get().getLocalizedDisplay() == null || fieldDto.get().getLocalizedDisplay().isEmpty()
        || StringUtils.isEmpty(fieldDto.get().getLocalizedDisplay().get(locale))) {
        return fieldDto.get().getDisplayName();
      }
      return fieldDto.get().getLocalizedDisplay().get(locale);
    }
    return mapping;
  }

  public static int indexOf(final String[] mappings, final String field) {
    return ArrayUtils.indexOf(mappings, field);
  }

  public static List<Integer> indexesOf(final String[] mappings, final String field) {
    List<Integer> indexes = new ArrayList<Integer>();
    int i = 0;
    for (String mapping : mappings) {
      if (match(field, mapping)) {
        indexes.add(i);
      }
      i++;
    }
    return indexes;
  }

  private static boolean match(final String field, String mapping) {
    return mapping.equals(field);
  }

}
