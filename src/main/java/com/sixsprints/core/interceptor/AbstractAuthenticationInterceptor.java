package com.sixsprints.core.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import com.sixsprints.core.annotation.Authenticated;
import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.enums.AccessPermission;
import com.sixsprints.core.enums.Restriction;
import com.sixsprints.core.exception.BaseException;
import com.sixsprints.core.exception.EntityNotFoundException;
import com.sixsprints.core.exception.NotAuthenticatedException;
import com.sixsprints.core.service.GenericCrudService;
import com.sixsprints.core.utils.ApplicationContext;
import com.sixsprints.core.utils.AuthUtil;

public abstract class AbstractAuthenticationInterceptor<T extends AbstractMongoEntity>
  implements AsyncHandlerInterceptor {

  private static final String USER = "user";
  private GenericCrudService<T> userService;

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
    if (!(method.getDeclaringClass().isAnnotationPresent(Authenticated.class)
      || method.isAnnotationPresent(Authenticated.class))) {
      return true;
    }
    Authenticated annotation = mergeAnnotationData(method);
    String token = httpServletRequest.getHeader(auhtTokenKey());
    if (!StringUtils.hasText(token)) {
      token = httpServletRequest.getParameter(auhtTokenKey());
    }
    T user = checkUser(annotation, token);
    checkRestriction(user, annotation.restriction());
    postProcessor(user);
    return true;
  }

  protected void checkRestriction(T user, Restriction restriction) throws BaseException {
  }

  protected abstract String auhtTokenKey();

  protected abstract void checkUserPermissions(T user, Authenticated authAnnotation)
    throws NotAuthenticatedException, EntityNotFoundException;

  protected abstract void checkIfTokenInvalid(T user, String token, Authenticated authAnnotation)
    throws NotAuthenticatedException;

  protected void postProcessor(T user) {
    ApplicationContext.setCurrentUser(user);
    if (user != null) {
      MDC.put(USER, user.getSlug());
    }
  }

  protected void throwException(Authenticated authAnnotation, String message) throws NotAuthenticatedException {
    if (authAnnotation.required()) {
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

  private T checkUser(Authenticated authAnnotation, String token)
    throws NotAuthenticatedException, EntityNotFoundException {
    Boolean tokenEmpty = checkIfTokenEmpty(authAnnotation, token);
    if (tokenEmpty) {
      return null;
    }
    T user = decodeUser(token, authAnnotation);
    if (user == null) {
      return null;
    }
    checkIfTokenInvalid(user, token, authAnnotation);
    checkIfActive(user, authAnnotation);
    checkUserPermissions(user, authAnnotation);
    return user;
  }

  private void checkIfActive(T user, Authenticated authAnnotation) throws NotAuthenticatedException {
    if (!user.getActive()) {
      throwException(authAnnotation, inactiveErrorMessage(user));
    }
  }

  private T decodeUser(String token, Authenticated authAnnotation) throws NotAuthenticatedException {
    T user = null;
    try {
      String userId = AuthUtil.decodeToken(token);
      user = userService.findOne(userId);
    } catch (BaseException ex) {
      throwException(authAnnotation, ex.getMessage());
    }
    return user;
  }

  private Boolean checkIfTokenEmpty(Authenticated authAnnotation, String token) throws NotAuthenticatedException {
    if (!StringUtils.hasText(token)) {
      throwException(authAnnotation, tokenEmptyErrorMessage());
      return true;
    }
    return false;
  }

  private Authenticated mergeAnnotationData(Method method) {
    Authenticated annotationClass = method.getDeclaringClass().getAnnotation(Authenticated.class);
    Authenticated annotationMethod = method.getAnnotation(Authenticated.class);

    if (annotationMethod == null) {
      return sanitize(annotationClass);
    }

    if (annotationClass == null) {
      return sanitize(annotationMethod);
    }

    return sanitize(new Authenticated() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Authenticated.class;
      }

      @Override
      public boolean required() {
        return annotationMethod.required();
      }

      @Override
      public String entity() {
        return StringUtils.hasText(annotationMethod.entity()) ? annotationMethod.entity() : annotationClass.entity();
      }

      @Override
      public Restriction restriction() {
        return Restriction.NULL == annotationMethod.restriction() ? annotationClass.restriction()
          : annotationMethod.restriction();
      }

      @Override
      public AccessPermission access() {
        return AccessPermission.NULL == annotationMethod.access() ? annotationClass.access()
          : annotationMethod.access();
      }
    });
  }

  private Authenticated sanitize(Authenticated authenticated) {
    return new Authenticated() {

      @Override
      public Class<? extends Annotation> annotationType() {
        return Authenticated.class;
      }

      @Override
      public boolean required() {
        return authenticated.required();
      }

      @Override
      public String entity() {
        return StringUtils.hasText(authenticated.entity()) ? authenticated.entity() : "ANY";
      }

      @Override
      public Restriction restriction() {
        return Restriction.NULL == authenticated.restriction() ? Restriction.NONE : authenticated.restriction();
      }

      @Override
      public AccessPermission access() {
        return AccessPermission.NULL == authenticated.access() ? AccessPermission.ANY : authenticated.access();
      }
    };
  }

}