package com.sixsprints.core.enums;

public enum DataType {

  NUMBER(false), TEXT(true), DATE(false), LINK(true), BOOLEAN(false), SELECT(true), AUTO_COMPLETE(true),
  TEXT_AREA(true), IMAGE(false);

  private boolean isSearchable;

  public boolean isSearchable() {
    return isSearchable;
  }

  private DataType(boolean isSearchable) {
    this.isSearchable = isSearchable;

  }

}
