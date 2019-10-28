package com.sixsprints.core.enums;

public enum UploadErrorType {

  ERROR("Error"), WARNING("Warning");

  private String displayName;

  private UploadErrorType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}