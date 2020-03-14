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

  public static final DateTimeZone DEFAULT_TIMEZONE = DateTimeZone.forID("+05:30");

  public static final String DEFAULT_DATE_PATTERN = "dd-MM-YYYY";

  public static final String DEFAULT_SHORT_DATE_PATTERN = "dd MMM";

  @Builder.Default
  private DateTimeZone timeZone = DEFAULT_TIMEZONE;

  @Builder.Default
  private String datePattern = DEFAULT_DATE_PATTERN;

  @Builder.Default
  private String shortDatePattern = DEFAULT_SHORT_DATE_PATTERN;

  public DateTime now() {
    return initDateFromDate(new Date());
  }

  public String dateToShortString(Date date) {
    return initDateFromDate(date).toString(shortDatePattern);
  }

  public String dateToString(Date date) {
    return initDateFromDate(date).toString(datePattern);
  }

  public String dateToStringWithFormat(Date date, String format) {
    return initDateFromDate(date).toString(format);
  }

  public DateTime stringToDate(String date) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(datePattern);
    return formatter.parseDateTime(date);
  }

  public DateTime startOfDay(Long instant) {
    return initDateFromLong(instant).millisOfDay().withMinimumValue();
  }

  public DateTime endOfDay(Long instant) {
    return initDateFromLong(instant).millisOfDay().withMaximumValue();
  }

  public DateTime initDateFromLong(Long instant) {
    return new DateTime(instant, timeZone);
  }

  public DateTime initDateFromDate(Date date) {
    return new DateTime(date, timeZone);
  }

}
