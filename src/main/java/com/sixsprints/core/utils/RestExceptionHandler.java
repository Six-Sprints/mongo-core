
package com.sixsprints.core.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.BaseRuntimeException;
import com.sixsprints.core.service.MessageSourceService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public abstract class RestExceptionHandler {

  private MessageSourceService messageSourceService;

  public RestExceptionHandler(MessageSourceService messageSourceService) {
    this.messageSourceService = messageSourceService;
  }

  @ExceptionHandler(value = { BaseException.class })
  protected ResponseEntity<?> handleBaseException(BaseException ex, HttpServletRequest request, Locale locale) {
    log.error(getErrorMessage(ex.getMessage(), ex.getArguments(), Locale.ENGLISH), ex);
    return RestUtil.errorResponse(ex.getData(), getErrorMessage(ex.getMessage(), ex.getArguments(), locale),
      ex.getHttpStatus());
  }

  @ExceptionHandler(value = { BaseRuntimeException.class })
  protected ResponseEntity<?> handleBaseRuntimeException(BaseRuntimeException ex, HttpServletRequest request,
    Locale locale) {
    log.error(getErrorMessage(ex.getMessage(), ex.getArguments(), Locale.ENGLISH), ex);
    return RestUtil.errorResponse(ex.getData(), getErrorMessage(ex.getMessage(), ex.getArguments(), locale),
      ex.getHttpStatus());
  }

  @ExceptionHandler(value = { Exception.class })
  protected ResponseEntity<?> handleUnknownException(Exception ex, HttpServletRequest request, Locale locale) {
    log.error(getErrorMessage(ex.getMessage()), ex);
    String errorMessage = getErrorMessage(messageSourceService.genericError(), locale);
    return RestUtil.errorResponse(null, errorMessage, BaseException.DEFAULT_HTTP_STATUS);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex,
    HttpServletRequest request, Locale locale) {
    ConstraintViolation<?> next = ex.getConstraintViolations().iterator().next();
    String field = getLastElement(next.getPropertyPath().iterator());
    String error = invalidFielErrorMessage(next, field);
    log.error(error);
    return RestUtil.errorResponse(null, error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request,
    Locale locale) {
    ObjectError objectError = ex.getBindingResult().getAllErrors().get(0);
    String field = "";
    if (objectError instanceof FieldError) {
      field = ((FieldError) objectError).getField();
    }
    FieldError fieldError = (FieldError) objectError;
    String error = getErrorMessage(fieldError.getDefaultMessage(), List.of(), locale);
    if (error == null || error.equals(fieldError.getDefaultMessage())) {
      error = "Request parameter anomaly. " + field + " " + fieldError.getDefaultMessage();
    }
    log.error(error);
    return RestUtil.errorResponse(null, error, HttpStatus.BAD_REQUEST);
  }

  protected String getErrorMessage(String key, List<Object> args, Locale locale) {
    return MessageSourceUtil.resolveMessage(messageSourceService, key, args, locale);
  }

  protected String getErrorMessage(String key, Locale locale) {
    return getErrorMessage(key, null, locale);
  }

  protected String getErrorMessage(String key) {
    return getErrorMessage(key, Locale.ENGLISH);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleConstraintVoilationException(HttpMessageNotReadableException exception,
    HttpServletRequest request) {
    String errorDetails = jsonInvalidErrorMessage(exception);

    if (exception.getCause() instanceof InvalidFormatException) {
      InvalidFormatException invalidFormatException = (InvalidFormatException) exception.getCause();
      if (invalidFormatException.getTargetType().isEnum()) {
        errorDetails = invalidEnumErrorMessage(invalidFormatException);
      }
    }
    return RestUtil.errorResponse(null, errorDetails, HttpStatus.BAD_REQUEST);
  }

  protected String invalidEnumErrorMessage(InvalidFormatException invalidFormatException) {
    return String.format("Invalid enum value: '%s' for the field: '%s'. The value must be one of: %s.",
      invalidFormatException.getValue(),
      invalidFormatException.getPath().get(invalidFormatException.getPath().size() - 1).getFieldName(),
      Arrays.toString(invalidFormatException.getTargetType().getEnumConstants()));
  }

  protected String jsonInvalidErrorMessage(HttpMessageNotReadableException exception) {
    return "Request body is empty or malformed";
  }

  protected String invalidFielErrorMessage(ConstraintViolation<?> next, String field) {
    return "Request parameter anomaly. " + field + " is invalid. " + next.getMessage();
  }

  protected String getLastElement(final Iterator<Node> itr) {
    Node lastElement = itr.next();
    while (itr.hasNext()) {
      lastElement = itr.next();
    }
    return lastElement.getName();
  }

}
