package com.sixsprints.core.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import com.sixsprints.core.annotation.DontAuthenticate;
import com.sixsprints.core.auth.AuthAnnotationDataDto;
import com.sixsprints.core.auth.Authenticated;
import com.sixsprints.core.auth.BasicModuleEnum;
import com.sixsprints.core.auth.BasicPermissionEnum;
import com.sixsprints.core.auth.ModuleDefinition;
import com.sixsprints.core.auth.PermissionDefinition;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.exception.NotAuthenticatedException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.core.utils.ApplicationContext;
import com.sixsprints.core.utils.AuthUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class AbstractAuthenticationInterceptor<T extends AbstractMongoEntity>
  implements AsyncHandlerInterceptor {

  private static final String USER = "user";
  private final GenericCrudService<T> userService;

  public AbstractAuthenticationInterceptor(GenericCrudService<T> userService) {
    this.userService = userService;
  }

  @Override
  public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
    Object handler) throws Exception {
    if (!(handler instanceof HandlerMethod)) {
      return true;
    }
    Method method = ((HandlerMethod) handler).getMethod();
    if (method.isAnnotationPresent(DontAuthenticate.class)) {
      return true;
    }
    AuthAnnotationDataDto annotationData = annotationData(method);

    if (annotationData == null) {
      return true;
    }

    String token = httpServletRequest.getHeader(authTokenKey());
    if (!StringUtils.hasText(token)) {
      token = httpServletRequest.getParameter(authTokenKey());
    }
    T user = checkUser(token, annotationData.isRequired());
    postProcessor(user);
    return true;
  }

  protected void postProcessor(T user) {
    ApplicationContext.setCurrentUser(user);
    if (user != null) {
      MDC.put(USER, user.getSlug());
    }
  }

  protected void throwException(boolean required, String message) throws NotAuthenticatedException {
    if (required) {
      throw NotAuthenticatedException.childBuilder().error(message).build();
    }
  }

  protected String unauthorisedErrorMessage(T user) {
    return "You are not authorized to take this action.";
  }

  protected String inactiveErrorMessage(T user) {
    return "User account is not active.";
  }

  protected String tokenInvalidErrorMessage() {
    return "Token is invalid!";
  }

  protected String tokenEmptyErrorMessage() {
    return "Token is empty!";
  }

  protected abstract String authTokenKey();

  protected abstract void checkUserPermissions(T user, ModuleDefinition module, PermissionDefinition permission,
    boolean required)
    throws NotAuthenticatedException, EntityNotFoundException;

  protected abstract void checkIfTokenInvalid(T user, String token, boolean required)
    throws NotAuthenticatedException;

  protected T checkUser(String token, boolean required)
    throws NotAuthenticatedException, EntityNotFoundException {
    Boolean tokenEmpty = checkIfTokenEmpty(token, required);
    if (tokenEmpty) {
      return null;
    }
    T user = decodeUser(token, required);
    if (user == null) {
      return null;
    }
    checkIfTokenInvalid(user, token, required);
    checkIfActive(user, required);
    checkUserPermissions(user, null, null, required);
    checkCustomAttributes(user, required);
    return user;
  }

  protected void checkCustomAttributes(T user, boolean required) throws NotAuthenticatedException {

  }

  protected void checkIfActive(T user, boolean required) throws NotAuthenticatedException {
    if (!user.getActive()) {
      throwException(required, inactiveErrorMessage(user));
    }
  }

  private T decodeUser(String token, boolean required) throws NotAuthenticatedException {
    T user = null;
    try {
      String userId = AuthUtil.decodeToken(token);
      user = userService.findOne(userId);
    } catch (BaseException ex) {
      throwException(required, ex.getMessage());
    }
    return user;
  }

  private Boolean checkIfTokenEmpty(String token, boolean required) throws NotAuthenticatedException {
    if (!StringUtils.hasText(token)) {
      throwException(required, tokenEmptyErrorMessage());
      return true;
    }
    return false;
  }

  private AuthAnnotationDataDto annotationData(Method method) {
    Annotation annotationClass = findRelevantAnnotation(method.getDeclaringClass().getAnnotations());
    Annotation annotationMethod = findRelevantAnnotation(method.getAnnotations());

    if (annotationClass == null && annotationMethod == null) {
      return null;
    }

    AuthAnnotationDataDto classData = fetchAnnotationData(annotationClass);
    AuthAnnotationDataDto methodData = fetchAnnotationData(annotationMethod);

    if (classData == null) {
      return sanitize(methodData);
    }

    if (methodData == null) {
      return sanitize(classData);
    }

    return sanitize(
      AuthAnnotationDataDto.builder()
        .module(methodData.getModule() == null ? classData.getModule() : methodData.getModule())
        .permission(methodData.getPermission() == null ? classData.getPermission() : methodData.getPermission())
        .required(methodData.isRequired())
        .build());
  }

  private Annotation findRelevantAnnotation(Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      if (annotation.annotationType().isAnnotationPresent(Authenticated.class)) {
        return annotation;
      }
    }
    return null;
  }

  private AuthAnnotationDataDto sanitize(AuthAnnotationDataDto annotationData) {
    return AuthAnnotationDataDto.builder()
      .module(annotationData.getModule() == null ? BasicModuleEnum.ANY : annotationData.getModule())
      .permission(annotationData.getPermission() == null ? BasicPermissionEnum.ANY : annotationData.getPermission())
      .required(annotationData.isRequired())
      .build();
  }

  private AuthAnnotationDataDto fetchAnnotationData(Annotation annotation) {

    if (annotation == null) {
      return null;
    }

    AuthAnnotationDataDto data = new AuthAnnotationDataDto();
    Method[] methods = annotation.annotationType().getMethods();
    for (Method method : methods) {

      String methodName = method.getName();

      String name = "module";
      if (name.equals(methodName)) {
        ModuleDefinition module = fetchSpecificData(annotation, method, name, ModuleDefinition.class);
        data.setModule(module);
      }

      name = "permission";
      if (name.equals(methodName)) {
        PermissionDefinition permission = fetchSpecificData(annotation, method, name, PermissionDefinition.class);
        data.setPermission(permission);
      }

      name = "required";
      if (name.equals(methodName)) {
        Boolean required = fetchSpecificData(annotation, method, name, boolean.class);
        if (required != null) {
          data.setRequired(required);
        } else {
          data.setRequired(true);
        }
      }
    }
    return data;
  }

  @SuppressWarnings("unchecked")
  private <Z> Z fetchSpecificData(Annotation annotation, Method method, String methodName, Class<Z> clazz) {
    String name = method.getName();
    Class<?> returnType = method.getReturnType();
    if (name.equals(methodName) && clazz.isAssignableFrom(returnType)) {
      Z data = null;
      try {
        data = (Z) (method.invoke(annotation, new Object[] {}));
      } catch (Exception e) {
      }
      return data;
    }
    return null;
  }

}