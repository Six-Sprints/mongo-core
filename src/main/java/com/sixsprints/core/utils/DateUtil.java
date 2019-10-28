package com.sixsprints.core.utils;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtil {

  public static final DateTimeZone IST = DateTimeZone.forID("+05:30");

  private static final String DEFAULT_DATE_PATTERN = "yyyy/MM/dd";

  private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy/MM/dd HH:mm:ss";

  public static String dateToString(Date date) {
    return initDateFromDate(date).toString(DEFAULT_DATE_PATTERN);
  }

  public static Date startOfDay(Long instant) {
    return initDateFromLong(instant).millisOfDay().withMinimumValue().toDate();
  }

  public static Date endOfDay(Long instant) {
    return initDateFromLong(instant).millisOfDay().withMaximumValue().toDate();
  }

  public static String format(Date date) {
    return initDateFromDate(date).toString(DEFAULT_DATE_PATTERN);
  }

  public static String formatToDateTime(Date date) {
    return initDateFromDate(date).toString(DEFAULT_DATE_TIME_PATTERN);
  }

  public static Date stringToDate(String date) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern(DEFAULT_DATE_TIME_PATTERN);
    return formatter.parseDateTime(date).toDate();
  }

  private static DateTime initDateFromLong(Long instant) {
    return new DateTime(instant, IST);
  }

  private static DateTime initDateFromDate(Date date) {
    return new DateTime(date, IST);
  }

}
