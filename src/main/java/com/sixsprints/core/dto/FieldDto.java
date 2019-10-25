package com.sixsprints.core.dto;

import java.util.Locale;
import java.util.Map;

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

  private boolean isPinned;

  private boolean isLocked;

  private ValidationConstraintDto validation;

  private boolean aggregationAllowed;

  private Map<Locale, String> localizedDisplay;

  @Override
  public int compareTo(FieldDto o) {
    return sequence - o.sequence;
  }
}
