
package com.sixsprints.core.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Singular;

public class NotAuthorizedException extends BaseException {

  private static final long serialVersionUID = -7565260959261747230L;

  private static final String DEFAULT_MESSAGE = "Not Authorized !";

  private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.UNAUTHORIZED;

  @Builder(builderMethodName = "childBuilder")
  public NotAuthorizedException(HttpStatus httpStatus, String error, Object data, @Singular List<Object> args) {
    super(checkIfNull(httpStatus, DEFAULT_HTTP_STATUS), checkIfNull(error, DEFAULT_MESSAGE), data, args);
  }

}
