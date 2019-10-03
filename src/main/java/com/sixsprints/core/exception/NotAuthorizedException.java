
package com.sixsprints.core.exception;

import org.springframework.http.HttpStatus;

import lombok.Builder;

public class NotAuthorizedException extends BaseException {

  private static final long serialVersionUID = -7565260959261747230L;

  private static final String DEFAULT_MESSAGE = "Not Authorized !";

  private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.UNAUTHORIZED;

  @Builder(builderMethodName = "childBuilder")
  public NotAuthorizedException(HttpStatus httpStatus, String error, Object[] arguments, Object data) {
    super(checkIfNull(httpStatus, DEFAULT_HTTP_STATUS), checkIfNull(error, DEFAULT_MESSAGE), arguments, data);
  }

}
