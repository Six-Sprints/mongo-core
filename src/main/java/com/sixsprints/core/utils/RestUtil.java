package com.sixsprints.core.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class RestUtil {

  private static <T> ResponseEntity<RestResponse<T>> response(T data, String message, HttpStatus statusCode,
    Boolean status) {
    return ResponseEntity.status(statusCode).<RestResponse<T>>body(
      RestResponse.<T>builder().message(message).data(data).status(status).build());
  }

  public static <T> ResponseEntity<RestResponse<T>> successResponse(T data) {
    return response(data, null, HttpStatus.OK, Boolean.TRUE);
  }

  public static <T> ResponseEntity<RestResponse<T>> successResponse(T data, HttpStatus statusCode) {
    return response(data, null, statusCode, Boolean.TRUE);
  }

  public static <T> ResponseEntity<RestResponse<T>> successResponse(T data, String message) {
    return response(data, message, HttpStatus.OK, Boolean.TRUE);
  }

  public static <T> ResponseEntity<RestResponse<T>> successResponse(T data, String message, HttpStatus statusCode) {
    return response(data, message, statusCode, Boolean.TRUE);
  }

  public static <T> ResponseEntity<RestResponse<T>> errorResponse(T data, String errorMessage, HttpStatus statusCode) {
    return response(data, errorMessage, statusCode, Boolean.FALSE);
  }
}
