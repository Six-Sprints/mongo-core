
package com.sixsprints.core.exception;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.sixsprints.core.constants.ExceptionConstants;

import lombok.Builder;
import lombok.Singular;

public class EntityAlreadyExistsException extends BaseException {

  private static final long serialVersionUID = 3642770990168988049L;

  private static final String DEFAULT_MESSAGE = ExceptionConstants.ENTITY_ALREADY_EXISTS;

  private static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.CONFLICT;

  @Builder(builderMethodName = "childBuilder")
  public EntityAlreadyExistsException(HttpStatus httpStatus, String error, Object data,
      @Singular List<Object> args) {
    super(checkIfNull(httpStatus, DEFAULT_HTTP_STATUS), checkIfNull(error, DEFAULT_MESSAGE), data,
        args);
  }

}
