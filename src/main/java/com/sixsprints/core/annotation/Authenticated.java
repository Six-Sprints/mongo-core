package com.sixsprints.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sixsprints.core.enums.AccessPermission;
import com.sixsprints.core.enums.Restriction;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authenticated {

  boolean required() default true;

  String entity() default "";

  AccessPermission access() default AccessPermission.NULL;

  Restriction restriction() default Restriction.NULL;

}
