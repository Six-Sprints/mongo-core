package com.sixsprints.core.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

  public static final ZoneId DEFAULT_TIMEZONE = ZoneId.systemDefault();

  public static final String DEFAULT_DATE_PATTERN = "dd-MM-yyyy";

  public static final String DEFAULT_SHORT_DATE_PATTERN = "dd MMM";

  @Builder.Default
  private ZoneId timeZone = DEFAULT_TIMEZONE;

  @Builder.Default
  private String datePattern = DEFAULT_DATE_PATTERN;

  @Builder.Default
  private String shortDatePattern = DEFAULT_SHORT_DATE_PATTERN;

  public ZonedDateTime now() {
    return initDateFromDate(LocalDateTime.now());
  }

  public String epochToShortString(Long epoch) {
    return epochToStringWithFormat(epoch, shortDatePattern);
  }

  public String epochToString(Long epoch) {
    return epochToStringWithFormat(epoch, datePattern);
  }

  public String epochToStringWithFormat(Long epoch, String format) {
    ZonedDateTime zoneDateTime = initDateFromEpoch(epoch);
    return zonedDateToString(format, zoneDateTime);
  }

  public String dateToShortString(LocalDateTime date) {
    return dateToStringWithFormat(date, shortDatePattern);
  }

  public String dateToString(LocalDateTime date) {
    return dateToStringWithFormat(date, datePattern);
  }

  public String dateToStringWithFormat(LocalDateTime date, String format) {
    ZonedDateTime zonedDate = initDateFromDate(date);
    return zonedDateToString(format, zonedDate);
  }

  public ZonedDateTime stringToDate(String date) {
    return stringToDateWithFormat(date, datePattern);
  }

  public Long stringToEpoch(String date) {
    return stringToEpochWithFormat(date, datePattern);
  }

  public ZonedDateTime stringToDateWithFormat(String date, String format) {
    return initDateFromDate(parseStringToLocalDateTime(date, format));
  }

  public Long stringToEpochWithFormat(String date, String format) {
    return initDateFromDate(parseStringToLocalDateTime(date, format)).toInstant().toEpochMilli();
  }

  public ZonedDateTime startOfDay(Long epoch) {
    return initDateFromEpoch(epoch).with(LocalTime.MIN);
  }

  public ZonedDateTime endOfDay(Long epoch) {
    return initDateFromEpoch(epoch).with(LocalTime.MAX);
  }

  public ZonedDateTime initDateFromEpoch(Long epoch) {
    Instant instant = Instant.ofEpochMilli(epoch);
    return ZonedDateTime.ofInstant(instant, timeZone);
  }

  public ZonedDateTime initDateFromDate(LocalDateTime date) {
    return ZonedDateTime.of(date, timeZone);
  }

  public LocalDateTime parseStringToLocalDateTime(String date, String format) {
    DateTimeFormatter formatter = initDateTimeFormatter(format);
    if (format.contains("HH:mm") || format.contains("hh:mm")) {
      return LocalDateTime.parse(date, formatter);
    }
    return LocalDate.parse(date, formatter).atStartOfDay();
  }

  private String zonedDateToString(String format, ZonedDateTime zonedDate) {
    DateTimeFormatter formatter = initDateTimeFormatter(format);
    return zonedDate.format(formatter);
  }

  private DateTimeFormatter initDateTimeFormatter(String format) {
    return DateTimeFormatter.ofPattern(format);
  }

}
