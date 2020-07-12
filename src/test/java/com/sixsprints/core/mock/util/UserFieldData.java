package com.sixsprints.core.mock.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.ImmutableMap;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.core.enums.DataType;
import com.sixsprints.core.mock.enums.Gender;

public class UserFieldData {

  public static List<FieldDto> fields() {

    List<FieldDto> fields = new ArrayList<FieldDto>();
    int i = 0;
    fields.add(FieldDto.builder().name("email").sequence(i++)
      .dataType(DataType.EMAIL).displayName("Email")
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Email", Locale.JAPANESE, "JapanEmail"))
      .build());

    fields.add(FieldDto.builder().name("name").sequence(i++)
      .dataType(DataType.TEXT)
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Name", Locale.JAPANESE, "JapanName")).build());

    fields.add(FieldDto.builder().name("flag").sequence(i++)
      .dataType(DataType.BOOLEAN)
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Flag", Locale.JAPANESE, "JapanFlag")).build());

    fields.add(FieldDto.builder().name("address.city").sequence(i++)
      .dataType(DataType.TEXT)
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "City", Locale.JAPANESE, "JapanCity")).build());

    fields.add(FieldDto.builder().name("dateCreated").sequence(i++)
      .dataType(DataType.DATE)
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Date", Locale.JAPANESE, "メール")).build());

    fields.add(FieldDto.builder().name("roleName").sequence(i++)
      .dataType(DataType.AUTO_COMPLETE).collectionName("role").columnName("name")
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Role Name", Locale.JAPANESE, "JapanRoleName"))
      .build());

    fields.add(FieldDto.builder().name("gender").sequence(i++)
      .dataType(DataType.ENUM).enumClass(Gender.class).allValues(Arrays.asList(Gender.values()))
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "Gender", Locale.JAPANESE, "JapanGender"))
      .build());

    fields.add(FieldDto.builder().name("customId").sequence(i++)
      .dataType(DataType.NUMBER)
      .localizedDisplay(ImmutableMap.<Locale, String>of(Locale.ENGLISH, "CustomID", Locale.JAPANESE, "CustomIDGender"))
      .build());

    return fields;
  }

}
