package com.sixsprints.core.mock.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.enums.DataType;

public class UserFieldData {

  public static List<FieldDto> fields() {

    List<FieldDto> fields = new ArrayList<FieldDto>();
    int i = 0;
    fields.add(FieldDto.builder().name("email").sequence(i++)
      .dataType(DataType.TEXT)
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Email", Locale.JAPANESE, "メール")).build());

    fields.add(FieldDto.builder().name("name").sequence(i++)
      .dataType(DataType.TEXT)
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Name", Locale.JAPANESE, "JapanName")).build());

    fields.add(FieldDto.builder().name("flag").sequence(i++)
      .dataType(DataType.TEXT)
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Flag", Locale.JAPANESE, "JapanFlag")).build());

    fields.add(FieldDto.builder().name("address.city").sequence(i++)
      .dataType(DataType.TEXT)
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "City", Locale.JAPANESE, "JapanCity")).build());

    return fields;
  }

}
