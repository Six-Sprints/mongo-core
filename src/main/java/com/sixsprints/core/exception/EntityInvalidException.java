
package com.sixsprints.core.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Singular;

public class EntityInvalidException extends BaseException {

  private static final long serialVersionUID = 1455288609270613866L;

  private static final String DEFAULT_MESSAGE = "Entity is invalid!";

  private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.NOT_ACCEPTABLE;

  @Builder(builderMethodName = "childBuilder")
  public EntityInvalidException(HttpStatus httpStatus, String error, Object data, @Singular List<Object> args) {
    super(checkIfNull(httpStatus, DEFAULT_HTTP_STATUS), checkIfNull(error, DEFAULT_MESSAGE), data, args);
  }

}
