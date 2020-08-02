package com.sixsprints.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sixsprints.core.utils.ApplicationContext;

@Component
public class ClearAllContextInterceptor extends HandlerInterceptorAdapter {

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