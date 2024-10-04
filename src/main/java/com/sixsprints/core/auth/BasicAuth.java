package com.sixsprints.core.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Authenticated
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface BasicAuth {
  
  boolean required() default true;

  BasicModuleEnum module() default BasicModuleEnum.ANY;
  
  BasicPermissionEnum permission() default BasicPermissionEnum.ANY;

}