package com.sixsprints.core.mock.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.enums.DataType;

public class AnimalFieldData {

  public static List<FieldDto> fields() {

    List<FieldDto> fields = new ArrayList<FieldDto>();
    int i = 0;

    fields.add(FieldDto.builder().name("name").sequence(i++)
      .dataType(DataType.TEXT)
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Name", Locale.JAPANESE, "JapanName")).build());

    fields.add(FieldDto.builder().name("count").sequence(i++)
      .dataType(DataType.NUMBER)
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Count", Locale.JAPANESE, "JapanCount"))
      .build());

    fields.add(FieldDto.builder().name("customId").sequence(i++)
      .dataType(DataType.NUMBER)
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "CustomId", Locale.JAPANESE, "JapanCustomId"))
      .build());

    return fields;
  }

}
