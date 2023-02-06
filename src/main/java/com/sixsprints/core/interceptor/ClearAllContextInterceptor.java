package com.sixsprints.core.interceptor;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import com.sixsprints.core.utils.ApplicationContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ClearAllContextInterceptor implements AsyncHandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object handler)
    throws Exception {
    process();
    return true;
  }

  protected void process() {
    ApplicationContext.clear();
    MDC.clear();
  }

}