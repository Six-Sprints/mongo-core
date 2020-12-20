package com.sixsprints.core.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.sixsprints.core.annotation.CharacterRestriction;

public class CharacterRestrictionConstraintValidator implements ConstraintValidator<CharacterRestriction, String> {

  private boolean mustOnlyContainAlphaNumericCharacters;

  private boolean allowNull;

  public static final String ONLY_ALPHA_NUMERIC_WITH_JP_SUPPORT_REGEX = "([a-z]|[A-Z]|[0-9]|[\\u3040-\\u309F]|[\\u30A0-\\u30FF]|[\\uFF00-\\uFFEF])+";

  @Override
  public void initialize(CharacterRestriction constraintAnnotation) {
    this.mustOnlyContainAlphaNumericCharacters = constraintAnnotation.mustOnlyContainAlphaNumericCharacters();
    this.allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {

    boolean isValid = true;

    if (allowNull == false && value == null) {
      return false;
    }

    if (allowNull == true && value == null) {
      return true;
    }

    if (mustOnlyContainAlphaNumericCharacters) {
      isValid = value.matches(ONLY_ALPHA_NUMERIC_WITH_JP_SUPPORT_REGEX);
    }

    return isValid;
  }

}
