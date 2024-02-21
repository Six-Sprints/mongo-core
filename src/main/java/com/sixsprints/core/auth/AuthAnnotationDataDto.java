package com.sixsprints.core.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthAnnotationDataDto {

  private ModuleDefinition module;

  private PermissionDefinition permission;

  private boolean required;

}
