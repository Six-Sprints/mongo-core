package com.sixsprints.core.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.sixsprints.core.annotation.EqualityConstraint;

public class EqualityConstraintValidator implements ConstraintValidator<EqualityConstraint, String> {

  private String mustBeEqualTo;

  @Override
  public void initialize(EqualityConstraint constraintAnnotation) {
    this.mustBeEqualTo = constraintAnnotation.mustBeEqualTo();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return mustBeEqualTo.equals(value);
  }

}
