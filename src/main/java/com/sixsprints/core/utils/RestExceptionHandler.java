
package com.sixsprints.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.BaseRuntimeException;
import com.sixsprints.core.service.MessageSourceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public abstract class RestExceptionHandler extends ResponseEntityExceptionHandler {

  protected abstract MessageSourceService messageSourceService();

  @ExceptionHandler(value = { BaseException.class })
  protected ResponseEntity<?> handleBaseException(BaseException ex, WebRequest request, Locale locale) {
    log.error(getErrorMessage(ex.getMessage(), ex.getArguments(), Locale.ENGLISH));
    return RestUtil.errorResponse(ex.getData(), getErrorMessage(ex.getMessage(), ex.getArguments(), locale),
      ex.getHttpStatus());
  }

  @ExceptionHandler(value = { BaseRuntimeException.class })
  protected ResponseEntity<?> handleBaseRuntimeException(BaseRuntimeException ex, WebRequest request, Locale locale) {
    log.error(getErrorMessage(ex.getMessage(), ex.getArguments(), Locale.ENGLISH));
    return RestUtil.errorResponse(ex.getData(), getErrorMessage(ex.getMessage(), ex.getArguments(), locale),
      ex.getHttpStatus());
  }

  @ExceptionHandler(value = { Exception.class })
  protected ResponseEntity<?> handleUnknownException(Exception ex, WebRequest request, Locale locale) {
    log.error(getErrorMessage(ex.getMessage()), ex);
    String errorMessage = getErrorMessage(messageSourceService().genericError(), locale);
    return RestUtil.errorResponse(null, errorMessage, BaseException.DEFAULT_HTTP_STATUS);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
    HttpStatus status, WebRequest request) {
    String violation = convertConstraintViolation(ex);
    return new ResponseEntity<Object>(RestResponse.builder().message(violation).status(false).build(),
      HttpStatus.BAD_REQUEST);
  }

  protected String convertConstraintViolation(MethodArgumentNotValidException ex) {
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    List<String> errorMessages = new ArrayList<String>();
    for (FieldError c : fieldErrors) {
      log.error(c.getDefaultMessage());
      errorMessages.add(getErrorMessage(c.getDefaultMessage(), messageSourceService().defaultLocale()));
    }
    if (errorMessages.size() == 1) {
      return errorMessages.get(0);
    }
    return errorMessages.toString();
  }

  protected String getErrorMessage(String key, List<Object> args, Locale locale) {
    try {
      String errorMessage = messageSourceService().messageSource().getMessage(key, args.toArray(), locale);
      return errorMessage;
    } catch (Exception ex) {
      return StringUtils.isEmpty(key) ? ex.getMessage() : key;
    }
  }

  protected String getErrorMessage(String key, Locale locale) {
    return getErrorMessage(key, null, locale);
  }

  protected String getErrorMessage(String key) {
    return getErrorMessage(key, Locale.ENGLISH);
  }
}
