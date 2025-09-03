package com.sixsprints.core.constants;

/**
 * Constants for exception messages and i18n keys
 */
public final class ExceptionConstants {

  private ExceptionConstants() {
    // Utility class
  }

  // Base exception messages
  public static final String DEFAULT_ERROR_MESSAGE = "exception.default.message";
  public static final String GENERIC_ERROR = "exception.generic.error";

  // Entity related exceptions
  public static final String ENTITY_INVALID = "exception.entity.invalid";
  public static final String ENTITY_NOT_FOUND = "exception.entity.not.found";
  public static final String ENTITY_NOT_FOUND_WITH_ID = "exception.entity.not.found.with.id";
  public static final String ENTITY_NOT_FOUND_CRITERIA = "exception.entity.not.found.criteria";
  public static final String ENTITY_ALREADY_EXISTS = "exception.entity.already.exists";
  public static final String ENTITY_ALREADY_EXISTS_WITH_FIELD =
      "exception.entity.already.exists.with.field";

  // Authentication/Authorization exceptions
  public static final String NOT_AUTHENTICATED = "exception.not.authenticated";
  public static final String NOT_AUTHORIZED = "exception.not.authorized";
  public static final String UNABLE_TO_VERIFY_TOKEN = "exception.auth.unable.to.verify.token";
  public static final String INVALID_TOKEN = "exception.auth.invalid.token";
  public static final String TOKEN_EXPIRED = "exception.auth.token.expired";
  public static final String USER_NOT_FOUND = "exception.auth.user.not.found";
  public static final String USER_NOT_FOUND_WITH_ID = "exception.auth.user.not.found.with.id";
  public static final String UNAUTHORIZED_ACTION = "exception.auth.unauthorized.action";
  public static final String USER_ACCOUNT_INACTIVE = "exception.auth.user.account.inactive";
  public static final String TOKEN_INVALID = "exception.auth.token.invalid";
  public static final String TOKEN_EMPTY = "exception.auth.token.empty";

  // Validation exceptions
  public static final String PARAMETER_ANOMALY = "exception.validation.parameter.anomaly";
  public static final String MIN_GREATER_THAN_MAX = "exception.validation.min.greater.than.max";

  // Rest exception handler messages
  public static final String REQUEST_PARAMETER_ANOMALY = "exception.rest.request.parameter.anomaly";
  public static final String REQUEST_BODY_EMPTY_OR_MALFORMED =
      "exception.rest.request.body.empty.or.malformed";
  public static final String INVALID_ENUM_VALUE = "exception.rest.invalid.enum.value";
  public static final String FIELD_INVALID = "exception.rest.field.invalid";

  // Converter exceptions
  public static final String UNABLE_TO_CONVERT = "exception.converter.unable.to.convert";

  // Application constants
  public static final String CSV_IMPORT_MESSAGE = "app.csv.import.message";

}
