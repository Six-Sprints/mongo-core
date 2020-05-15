package com.sixsprints.core.utils;

public interface AppConstants {

  String BLANK_STRING = "(blank)";

  String EQUALS = "equals";

  String NOT_EQUAL = "notEqual";
  
  String EXISTS = "exists";
  
  String DOES_NOT_EXIST = "doesNotExist";

  String LESS_THAN = "lessThan";

  String LESS_THAN_OR_EQUAL = "lessThanOrEqual";

  String GREATER_THAN = "greaterThan";

  String GREATER_THAN_OR_EQUAL = "greaterThanOrEqual";

  String IN_RANGE = "inRange";

  String AND_OPERATOR = "AND";

  String OR_OPERATOR = "OR";

  String INHERITANCE_CRITERIA = "_class";

  int TOKEN_EXPIRY_IN_DAYS = 30;

  String CSV_IMPORT_MESSAGE = "CSV file import processing is complete. Number of rows successfully processed: %d (number of error cells: %d), number of rows failed in processing: %d. See the CSV error log for details.";

}
