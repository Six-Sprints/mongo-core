package com.sixsprints.core.dto;

import com.sixsprints.core.enums.DataType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, of = { "name" })
public class FieldDto implements Comparable<FieldDto> {

  private String name;

  private int sequence;

  private DataType dataType;

  private boolean isHidden;

  private boolean isHiddenExport;

  private boolean isMandatoryForDynamic;

  private boolean isPinned;

  private boolean isLocked;

  private ValidationConstraintDto validation;

  private boolean aggregationAllowed;

  @Override
  public int compareTo(FieldDto o) {
    return sequence - o.sequence;
  }
}
