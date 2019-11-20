
package com.sixsprints.core.utils;

import java.security.MessageDigest;

import com.sixsprints.core.exception.BaseRuntimeException;

public class EncryptionUtil {

  private static final String DEFAULT_ENCRYPTION_ALGORITHM = "SHA-1";

  public static String encrypt(String input) {
    return encrypt(input, DEFAULT_ENCRYPTION_ALGORITHM);
  }

  public static String encrypt(String input, String algorithmName) {
    try {
      MessageDigest md = MessageDigest.getInstance(algorithmName);
      md.update(input.getBytes());
      byte[] mdbytes = md.digest();
      StringBuffer output = new StringBuffer();
      for (int i = 0; i < mdbytes.length; i++) {
        output.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      return output.toString();
    } catch (Exception ex) {
      throw BaseRuntimeException.builder().error(ex.getMessage()).build();
    }
  }

}
