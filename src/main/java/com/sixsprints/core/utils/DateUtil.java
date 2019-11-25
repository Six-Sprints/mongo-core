package com.sixsprints.core.utils;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(builderMethodName = "instance")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtil {

  private static final DateTimeZone IST = DateTimeZone.forID("+05:30");

  private static final String DEFAULT_DATE_PATTERN = "yyyy/MM/dd";

  @Builder.Default
  private DateTimeZone timeZone = IST;

  @Builder.Default
  private String datePattern = DEFAULT_DATE_PATTERN;

  public DateTime now() {
    return initDateFromDate(new Date());
  }

  public String dateToString(Date date) {
    return initDateFromDate(date).toString(datePattern);
  }

  public DateTime startOfDay(Long instant) {
    return initDateFromLong(instant).millisOfDay().withMinimumValue();
  }

  public DateTime endOfDay(Long instant) {
    return initDateFromLong(instant).millisOfDay().withMaximumValue();
  }

  public String format(Date date) {
    return initDateFromDate(date).toString(datePattern);
  }

  public DateTime stringToDate(String date) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(datePattern);
    return formatter.parseDateTime(date);
  }

  private DateTime initDateFromLong(Long instant) {
    return new DateTime(instant, timeZone);
  }

  private DateTime initDateFromDate(Date date) {
    return new DateTime(date, timeZone);
  }

}
