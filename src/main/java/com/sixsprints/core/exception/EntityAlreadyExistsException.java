
package com.sixsprints.core.exception;

import org.springframework.http.HttpStatus;

import lombok.Builder;

public class EntityAlreadyExistsException extends BaseException {

  private static final long serialVersionUID = 3642770990168988049L;

  private static final String DEFAULT_MESSAGE = "Already exists !";

  private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.CONFLICT;

  @Builder(builderMethodName = "childBuilder")
  public EntityAlreadyExistsException(HttpStatus httpStatus, String error, Object[] arguments, Object data) {
    super(checkIfNull(httpStatus, DEFAULT_HTTP_STATUS), checkIfNull(error, DEFAULT_MESSAGE), arguments, data);
  }

}
