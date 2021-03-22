package com.sixsprints.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sixsprints.core.exception.NotAuthorizedException;
import com.sixsprints.core.utils.AuthUtil;

public class AuthUtilsTest {

  private static final String RANDOM_TOKEN = "random_token";

  private static final String DEFAULT_PAYLOAD = "payload";

  @Test
  public void shouldCreateAndDecodeToken() throws NotAuthorizedException {
    String token = AuthUtil.createToken(DEFAULT_PAYLOAD);
    assertThat(DEFAULT_PAYLOAD).isEqualTo(AuthUtil.decodeToken(token));
  }

  @Test
  public void shouldCreateAndDecodeTokenWithExpiryProvided() throws NotAuthorizedException {
    String token = AuthUtil.createToken(DEFAULT_PAYLOAD, 1);
    assertThat(DEFAULT_PAYLOAD).isEqualTo(AuthUtil.decodeToken(token));
  }

  @Test
  public void shouldThrowExceptionBecauseOfWrongToken() throws NotAuthorizedException {
    AuthUtil.createToken(DEFAULT_PAYLOAD);
    Assertions.assertThrows(NotAuthorizedException.class, () -> {
      AuthUtil.decodeToken(RANDOM_TOKEN);
    });
  }

  @Test
  public void shouldThrowExceptionBecauseOfExpiredToken() throws NotAuthorizedException {
    String token = AuthUtil.createToken(DEFAULT_PAYLOAD, -1);
    Assertions.assertThrows(NotAuthorizedException.class, () -> {
      AuthUtil.decodeToken(token);
    });

  }

}
