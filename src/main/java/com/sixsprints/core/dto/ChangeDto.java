package com.sixsprints.core.dto;

import com.sixsprints.core.enums.AuditLogAction;
import com.sixsprints.core.enums.AuditLogSource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDto {

  private AuditLogAction action;

  private AuditLogSource source;

  private String oldValue;

  private String newValue;

  private String propChanged;

}
