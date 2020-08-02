package com.sixsprints.core.utils;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.RequestContext;

public class ApplicationContext {

  protected static InheritableThreadLocal<Object> userData = new InheritableThreadLocal<>();

  protected static InheritableThreadLocal<RequestContext> requestData = new InheritableThreadLocal<>();

  public static <T extends AbstractMongoEntity> void setCurrentUser(T user) {
    userData.set(user);
  }

  @SuppressWarnings("unchecked")
  public static <T extends AbstractMongoEntity> T getCurrentUser() {
    return (T) userData.get();
  }

  public static void setCurrentRequest(RequestContext requestContext) {
    requestData.set(requestContext);
  }

  public static RequestContext getCurrentRequest() {
    RequestContext requestContext = requestData.get();
    if (requestContext == null) {
      requestContext = RequestContext.builder().build();
    }
    return requestContext;
  }

}