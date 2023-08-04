package com.sixsprints.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.sixsprints.core.validator.EqualityConstraintValidator;

@Documented
@Constraint(validatedBy = EqualityConstraintValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface EqualityConstraint {

  String message() default "must be equal to";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String mustBeEqualTo();

}