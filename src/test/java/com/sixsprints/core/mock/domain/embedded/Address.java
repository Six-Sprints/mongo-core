package com.sixsprints.core.mock.domain.embedded;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Address implements Serializable {

  private static final long serialVersionUID = 1798451283307034905L;

  private String city;

  private String state;

  private String country;

}
