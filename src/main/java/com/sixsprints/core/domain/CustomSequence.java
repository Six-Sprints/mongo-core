
package com.sixsprints.core.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Document(collection = "customsequences")
public class CustomSequence {

  @Id
  private String id;

  @Indexed
  private long seq;

}
