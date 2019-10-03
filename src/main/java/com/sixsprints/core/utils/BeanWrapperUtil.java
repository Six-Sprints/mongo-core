package com.sixsprints.core.utils;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessorFactory;

public class BeanWrapperUtil {

  public static void copyProperties(Object src, Object target, Iterable<String> props) {

    BeanWrapper srcWrap = PropertyAccessorFactory.forBeanPropertyAccess(src);
    BeanWrapper trgWrap = PropertyAccessorFactory.forBeanPropertyAccess(target);

    props.forEach(p -> trgWrap.setPropertyValue(p, srcWrap.getPropertyValue(p)));

  }

  public static Object getValue(Object obj, String prop) {
    BeanWrapper wrap = PropertyAccessorFactory.forBeanPropertyAccess(obj);
    return wrap.getPropertyValue(prop);
  }

  public static void copyNonNullProperties(Object src, Object target) {
    BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
  }

  private static String[] getNullPropertyNames(Object source) {
    final BeanWrapper src = new BeanWrapperImpl(source);
    PropertyDescriptor[] pds = src.getPropertyDescriptors();

    Set<String> emptyNames = new HashSet<String>();
    for (PropertyDescriptor pd : pds) {
      Object srcValue = src.getPropertyValue(pd.getName());
      if (srcValue == null || srcValue.toString().isEmpty())
        emptyNames.add(pd.getName());
    }
    String[] result = new String[emptyNames.size()];
    return emptyNames.toArray(result);
  }

  public static boolean checkAllPropsEqual(Object obj1, Object obj2) {
    if (obj1 == obj2) {
      return true;
    }
    if (!obj1.getClass().equals(obj2.getClass())) {
      return false;
    }

    final BeanWrapper obj1Wrap = new BeanWrapperImpl(obj1);
    final BeanWrapper obj2Wrap = new BeanWrapperImpl(obj2);

    PropertyDescriptor[] pds = obj1Wrap.getPropertyDescriptors();

    for (PropertyDescriptor pd : pds) {
      Object obj1Val = obj1Wrap.getPropertyValue(pd.getName());
      Object obj2Val = obj2Wrap.getPropertyValue(pd.getName());

      if (obj1Val == null && obj2Val == null) {
        continue;
      }

      if (obj1Val == null || obj2Val == null || !obj1Val.equals(obj2Val)) {
        return false;
      }
    }
    return true;
  }

}
