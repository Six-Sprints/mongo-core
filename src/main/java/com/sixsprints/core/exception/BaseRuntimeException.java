
package com.sixsprints.core.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BaseRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1341122794445989748L;

  public static final HttpStatus DEFAULT_HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

  public static final String DEFAULT_MESSAGE = "Something Bad Happened !";

  @Builder.Default
  private HttpStatus httpStatus = DEFAULT_HTTP_STATUS;

  @Builder.Default
  private String error = DEFAULT_MESSAGE;

  private Object[] arguments;

  private Object data;

  @Override
  public String getMessage() {
    return String.format(error, arguments);
  }

  protected static HttpStatus checkIfNull(HttpStatus httpStatus, HttpStatus defaultStatus) {
    return httpStatus == null ? defaultStatus : httpStatus;
  }

  protected static String checkIfNull(String error, String defaultMessage) {
    return error == null ? defaultMessage : error;
  }

}
