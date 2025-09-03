
package com.sixsprints.core.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.BaseRuntimeException;
import com.sixsprints.core.constants.ExceptionConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public abstract class RestExceptionHandler {

  private final MessageSource messageSource;

  @ExceptionHandler(value = {BaseException.class})
  protected ResponseEntity<?> handleBaseException(BaseException ex, HttpServletRequest request,
      Locale locale) {
    log.error(getErrorMessage(ex.getMessage(), ex.getArguments(), Locale.ENGLISH));
    return RestUtil.errorResponse(ex.getData(),
        getErrorMessage(ex.getMessage(), ex.getArguments(), locale), ex.getHttpStatus());
  }

  @ExceptionHandler(value = {BaseRuntimeException.class})
  protected ResponseEntity<?> handleBaseRuntimeException(BaseRuntimeException ex,
      HttpServletRequest request, Locale locale) {
    log.error(getErrorMessage(ex.getMessage(), ex.getArguments(), Locale.ENGLISH));
    return RestUtil.errorResponse(ex.getData(),
        getErrorMessage(ex.getMessage(), ex.getArguments(), locale), ex.getHttpStatus());
  }

  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<?> handleUnknownException(Exception ex, HttpServletRequest request,
      Locale locale) {
    log.error(getErrorMessage(ex.getMessage()));
    String errorMessage = getErrorMessage(ExceptionConstants.GENERIC_ERROR, locale);
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
  public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpServletRequest request, Locale locale) {
    ObjectError objectError = ex.getBindingResult().getAllErrors().get(0);
    String field = "";
    if (objectError instanceof FieldError) {
      field = ((FieldError) objectError).getField();
    }
    FieldError fieldError = (FieldError) objectError;
    String error = getErrorMessage(fieldError.getDefaultMessage(), List.of(), locale);
    if (error == null || error.equals(fieldError.getDefaultMessage())) {
      error = getErrorMessage(ExceptionConstants.REQUEST_PARAMETER_ANOMALY,
          List.of(field, fieldError.getDefaultMessage()), locale);
    }
    log.error(error);
    return RestUtil.errorResponse(null, error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleConstraintVoilationException(
      HttpMessageNotReadableException exception, HttpServletRequest request) {
    String errorDetails = jsonInvalidErrorMessage(exception);

    if (exception.getCause() instanceof InvalidFormatException) {
      InvalidFormatException invalidFormatException = (InvalidFormatException) exception.getCause();
      if (invalidFormatException.getTargetType().isEnum()) {
        errorDetails = invalidEnumErrorMessage(invalidFormatException);
      }
    }
    log.error(errorDetails);
    return RestUtil.errorResponse(null, errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<?> handleMissingParameterException(
      MissingServletRequestParameterException ex) {
    log.error(ex.getMessage(), ex);
    return RestUtil.errorResponse(null, ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  protected String getErrorMessage(String key, List<Object> args, Locale locale) {
    if (args == null) {
      args = List.of();
    }
    try {
      return messageSource.getMessage(key, args.toArray(), locale);
    } catch (Exception e) {
      return key;
    }
  }

  protected String getErrorMessage(String key, Locale locale) {
    return getErrorMessage(key, null, locale);
  }

  protected String getErrorMessage(String key) {
    return getErrorMessage(key, Locale.ENGLISH);
  }


  protected String invalidEnumErrorMessage(InvalidFormatException invalidFormatException) {
    return getErrorMessage(ExceptionConstants.INVALID_ENUM_VALUE,
        List.of(invalidFormatException.getValue(),
            invalidFormatException.getPath().get(invalidFormatException.getPath().size() - 1)
                .getFieldName(),
            Arrays.toString(invalidFormatException.getTargetType().getEnumConstants())),
        Locale.ENGLISH);
  }

  protected String jsonInvalidErrorMessage(HttpMessageNotReadableException exception) {
    return getErrorMessage(ExceptionConstants.REQUEST_BODY_EMPTY_OR_MALFORMED, Locale.ENGLISH);
  }

  protected String invalidFielErrorMessage(ConstraintViolation<?> next, String field) {
    return getErrorMessage(ExceptionConstants.FIELD_INVALID, List.of(field, next.getMessage()),
        Locale.ENGLISH);
  }

  protected String getLastElement(final Iterator<Node> itr) {
    Node lastElement = itr.next();
    while (itr.hasNext()) {
      lastElement = itr.next();
    }
    return lastElement.getName();
  }

}
