
package com.sixsprints.core.exception;

import org.springframework.http.HttpStatus;

import lombok.Builder;

public class NotAuthenticatedException extends BaseException {

  private static final long serialVersionUID = 763801764959412956L;

  private static final String DEFAULT_MESSAGE = "Not authenticated !";

  private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.FORBIDDEN;

  @Builder(builderMethodName = "childBuilder")
  public NotAuthenticatedException(HttpStatus httpStatus, String error, Object data, Object... arguments) {
    super(checkIfNull(httpStatus, DEFAULT_HTTP_STATUS), checkIfNull(error, DEFAULT_MESSAGE), data, arguments);
  }

}
