
package com.sixsprints.core.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path.Node;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.BaseRuntimeException;
import com.sixsprints.core.service.MessageSourceService;

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
    log.error(getErrorMessage(ex.getMessage(), ex.getArguments(), Locale.ENGLISH));
    return RestUtil.errorResponse(ex.getData(), getErrorMessage(ex.getMessage(), ex.getArguments(), locale),
      ex.getHttpStatus());
  }

  @ExceptionHandler(value = { BaseRuntimeException.class })
  protected ResponseEntity<?> handleBaseRuntimeException(BaseRuntimeException ex, HttpServletRequest request,
    Locale locale) {
    log.error(getErrorMessage(ex.getMessage(), ex.getArguments(), Locale.ENGLISH));
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
    String error = "Request Parameter anomaly. " + field + " is invalid. " + next.getMessage();
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
    String code = objectError.getCode();
    boolean isEmpty = "NotNull".equalsIgnoreCase(code) || "NotEmpty".equalsIgnoreCase(code);
    String errorDescription = isEmpty ? "not entered." : "invalid. " + field + " " + objectError.getDefaultMessage();
    String error = "Request Parameter anomaly. " + field + " is " + errorDescription;
    log.error(error);
    return RestUtil.errorResponse(null, error, HttpStatus.BAD_REQUEST);
  }

  private String getLastElement(final Iterator<Node> itr) {
    Node lastElement = itr.next();
    while (itr.hasNext()) {
      lastElement = itr.next();
    }
    return lastElement.getName();
  }

  protected String getErrorMessage(String key, List<Object> args, Locale locale) {
    try {
      String errorMessage = messageSourceService.messageSource().getMessage(key, args.toArray(), locale);
      return errorMessage;
    } catch (Exception ex) {
      return StringUtils.hasText(key) ? key : ex.getMessage();
    }
  }

  protected String getErrorMessage(String key, Locale locale) {
    return getErrorMessage(key, null, locale);
  }

  protected String getErrorMessage(String key) {
    return getErrorMessage(key, Locale.ENGLISH);
  }
}
