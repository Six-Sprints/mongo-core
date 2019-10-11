package com.sixsprints.core.utils;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateUtil {

  public static final DateTimeZone JAPAN_TIME_ZONE = DateTimeZone.forID("+09:00");

  private static final String DEFAULT_DATE_PATTERN = "yyyy/MM/dd";

  public static String dateToString(Date date) {
    return initDateFromDate(date).toString(DEFAULT_DATE_PATTERN);
  }

  public static Date startOfDay(Long instant) {
    return initDateFromLong(instant).millisOfDay().withMinimumValue().toDate();
  }

  public static Date endOfDay(Long instant) {
    return initDateFromLong(instant).millisOfDay().withMaximumValue().toDate();
  }
  
  private static DateTime initDateFromLong(Long instant) {
    return new DateTime(instant, JAPAN_TIME_ZONE);
  }
  
  private static DateTime initDateFromDate(Date date) {
    return new DateTime(date, JAPAN_TIME_ZONE);
  }

}
