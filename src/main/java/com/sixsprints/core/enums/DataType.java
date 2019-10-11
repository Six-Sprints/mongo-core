package com.sixsprints.core.enums;

public enum DataType {

  NUMBER(false), TEXT(true), DATE(false), STATUS(true), ROLE(true), LINK(true), BOOLEAN(false), AUTO_COMPLETE(true),
  TEXT_AREA(true);

  private boolean isSearchable;

  public boolean isSearchable() {
    return isSearchable;
  }

  private DataType(boolean isSearchable) {
    this.isSearchable = isSearchable;

  }

}
