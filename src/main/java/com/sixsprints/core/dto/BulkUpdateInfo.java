package com.sixsprints.core.dto;

import com.sixsprints.core.enums.UpdateAction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUpdateInfo<T> {

  private T data;

  private UpdateAction updateAction;

}
