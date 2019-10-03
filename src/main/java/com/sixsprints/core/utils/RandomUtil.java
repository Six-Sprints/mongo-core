
package com.sixsprints.core.utils;

import java.util.Date;
import java.util.Random;

public class RandomUtil {

  private static final char[] CHARS = "abcdefghijklmnpqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ123456789".toCharArray();

  private static Random random = new Random();

  public static int randomInt(int min, int max) {
    if (min > max) {
      throw new IllegalArgumentException("min cannot be greater than max");
    }
    if (min == max) {
      return min;
    }
    return random.nextInt(max - min) + min;
  }

  public static double randomDouble(double min, double max, int scale) {
    if (min > max) {
      throw new IllegalArgumentException("min = " + min + " cannot be greater than max =" + max);
    }
    if (min == max) {
      return min;
    }
    int intMax = (int) ((max - min) * Math.pow(10, scale));
    return min + (double) (random.nextInt(intMax)) / Math.pow(10, scale);
  }

  public static String randomAlphaNumericString(int length) {
    StringBuilder string = new StringBuilder();
    for (int i = 0; i < length; i++) {
      string.append(CHARS[randomInt(0, CHARS.length)]);
    }
    return string.toString();
  }

  public static Date randomDate(int min, int max) {
    return new Date(new Date().getTime() - (long) randomInt(min, max) * 1000);
  }

}
