package com.sixsprints.core.utils;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.query.Criteria;

public class InheritanceMongoUtil {

  public static Criteria generate(Class<?> classType) {
    if (classType.isAnnotationPresent(Subclass.class)) {
      String criteria = (classType.isAnnotationPresent(TypeAlias.class))
        ? classType.getAnnotation(TypeAlias.class).value()
        : classType.getName();
      return Criteria.where(AppConstants.INHERITANCE_CRITERIA).is(criteria);
    }
    return null;
  }

}
