package com.sixsprints.core.mock.domain.inheritance;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.utils.Subclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Document(collection = "animal")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Subclass
public class Tiger extends Animal {

  private static final long serialVersionUID = -7726198540610690812L;

  private Integer runningSpeed;

}
