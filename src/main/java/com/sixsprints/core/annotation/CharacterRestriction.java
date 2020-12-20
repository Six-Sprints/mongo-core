package com.sixsprints.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.sixsprints.core.validator.CharacterRestrictionConstraintValidator;

@Documented
@Constraint(validatedBy = CharacterRestrictionConstraintValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CharacterRestriction {

  String message() default "should not contain spaces or special characters";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  boolean mustOnlyContainAlphaNumericCharacters() default true;

  boolean allowNull() default false;

}