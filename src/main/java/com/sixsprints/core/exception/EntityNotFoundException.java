
package com.sixsprints.core.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Singular;

public class EntityNotFoundException extends BaseException {

  private static final long serialVersionUID = 1455288609270613866L;

  private static final String DEFAULT_MESSAGE = "Entity not found !";

  private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.NOT_FOUND;

  @Builder(builderMethodName = "childBuilder")
  public EntityNotFoundException(HttpStatus httpStatus, String error, Object data, @Singular List<Object> args) {
    super(checkIfNull(httpStatus, DEFAULT_HTTP_STATUS), checkIfNull(error, DEFAULT_MESSAGE), data, args);
  }

}
