package com.sixsprints.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.sixsprints.core.utils.EncryptionUtil;

public class EncryptionUtilTest {

  private static final String STRING_TO_ENCRYPT = "123";
  private static final String ENCRYPTED_STRING_MD5 = "202cb962ac59075b964b07152d234b70";
  private static final String ENCRYPTED_STRING_SHA = "40bd001563085fc35165329ea1ff5c5ecbdbbeef";

  @Test
  public void shouldEncrypt() {
    String encrypted = EncryptionUtil.encrypt(STRING_TO_ENCRYPT);
    assertThat(encrypted).isEqualTo(ENCRYPTED_STRING_SHA);
  }

  @Test
  public void shouldEncryptWithDesiredEncryption() {
    String encrypted = EncryptionUtil.encrypt(STRING_TO_ENCRYPT, "MD5");
    assertThat(encrypted).isEqualTo(ENCRYPTED_STRING_MD5);
  }

}
