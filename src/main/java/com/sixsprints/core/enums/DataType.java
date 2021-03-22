package com.sixsprints.core.enums;

import java.util.Date;

public enum DataType {

  NUMBER(false, Long.class), TEXT(true, String.class), DATE(false, Date.class), LINK(true, String.class),
  BOOLEAN(false, Boolean.class), SELECT(true, String.class), AUTO_COMPLETE(true, String.class),
  TEXT_AREA(true, String.class), IMAGE(false, String.class), EMAIL(true, String.class), ENUM(true, String.class);

  private boolean isSearchable;

  private Class<?> classType;

  public boolean isSearchable() {
    return isSearchable;
  }

  public Class<?> getClassType() {
    return classType;
  }

  private DataType(boolean isSearchable, Class<?> classType) {
    this.isSearchable = isSearchable;
    this.classType = classType;
  }

}
