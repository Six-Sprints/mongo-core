package com.sixsprints.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class RequestUtils {

  public static String getSelfUrl(HttpServletRequest request) {
    if (request.getQueryString() != null) {
      return request.getRequestURL() + "?" + request.getQueryString();
    }
    return request.getRequestURL().toString();
  }

  public static String getServer(HttpServletRequest request) {
    int port = request.getServerPort();
    StringBuilder result = new StringBuilder();
    result.append(request.getScheme())
      .append("://")
      .append(request.getServerName());
    if (port != 80) {
      result.append(':')
        .append(port);
    }
    return result.toString();
  }

  public static String getRequestId(String prefix) {
    return String.format("%s-%s-%s", prefix, ThreadLocalRandom.current().nextFloat(),
      System.currentTimeMillis());
  }

  public static Map<String, String[]> getHeadersMap(HttpServletRequest request) {
    Map<String, String[]> headersMap = new LinkedHashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String header = headerNames.nextElement();
      headersMap.put(header, enumerationToString(request.getHeaders(header)));
    }
    return headersMap;
  }

  public static String[] enumerationToString(Enumeration<String> enumeration) {
    List<String> headers = new ArrayList<>();
    while (enumeration.hasMoreElements()) {
      headers.add(enumeration.nextElement());
    }
    return headers.toArray(new String[headers.size()]);
  }

  public static String getRemoteAddress(HttpServletRequest request) {
    String ipAddress = request.getHeader("X-FORWARDED-FOR");
    if (ipAddress == null) {
      ipAddress = request.getRemoteAddr();
    }
    if (ipAddress != null) {
      ipAddress = truncate(ipAddress, 40);
    }
    assert ipAddress != null;
    int indexOfComma = ipAddress.indexOf(",");
    if (indexOfComma > -1) {
      ipAddress = ipAddress.substring(0, indexOfComma);
    }
    return ipAddress;
  }

  public static String getCookie(String key, HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(key)) {
        return cookie.getValue();
      }
    }
    return null;
  }

  public static String[] getParams(String key, HttpServletRequest request) {
    String[] params = request.getParameterMap().get(key);
    if (params == null) {
      return null;
    }
    return params;
  }

  public static String getParam(String key, HttpServletRequest request) {
    String[] params = getParams(key, request);
    if (params == null) {
      return null;
    }
    return params[0];
  }

  public static String truncate(String value, int length) {
    // Ensure String length is longer than requested size.
    if (value.length() > length) {
      return value.substring(0, length);
    } else {
      return value;
    }
  }

  public static String getPlatform(HttpServletRequest request, String platformHeaderKey) {
    String platformString = request.getHeader(platformHeaderKey);
    if (StringUtils.isBlank(platformString)) {
      return null;
    }
    return platformString;
  }

  public static String getClientOS(HttpServletRequest request, String clientOSHeaderKey) {
    String clientOSString = request.getHeader(clientOSHeaderKey);
    if (StringUtils.isBlank(clientOSString)) {
      return null;
    }
    return clientOSString;
  }

  public static String getAppType(HttpServletRequest request, String clientNameKey) {
    String appTypeString = request.getHeader(clientNameKey);
    if (StringUtils.isBlank(appTypeString)) {
      return null;
    }
    return appTypeString;
  }

  public static String getUtmInfo(HttpServletRequest request, String utmHeaderKey, boolean decode)
    throws UnsupportedEncodingException {
    String utmInfoString = request.getHeader(utmHeaderKey);
    if (StringUtils.isBlank(utmInfoString)) {
      return null;
    }
    return decode ? URLDecoder.decode(utmInfoString, "UTF-8") : utmInfoString;
  }
}
