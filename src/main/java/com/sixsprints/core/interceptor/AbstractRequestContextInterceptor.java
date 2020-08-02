package com.sixsprints.core.interceptor;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sixsprints.core.dto.RequestContext;
import com.sixsprints.core.utils.RequestUtils;
import com.sixsprints.core.utils.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractRequestContextInterceptor extends HandlerInterceptorAdapter {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse,
    Object handler) {
    RequestContext requestContext = RequestContext.builder()
      .requestId(RequestUtils.getRequestId(prefix()))
      .startTime(new Date().getTime())
      .selfUrl(RequestUtils.getSelfUrl(request))
      .remoteAddress(RequestUtils.getRemoteAddress(request))
      .localAddress(request.getLocalAddr())
      .remoteHost(request.getRemoteHost())
      .server(RequestUtils.getServer(request))
      .uri(request.getRequestURI())
      .queryString(request.getQueryString())
      .userAgent(request.getHeader("User-Agent"))
      .httpMethod(request.getMethod())
      .headersMap(RequestUtils.getHeadersMap(request))
      .parametersMap(request.getParameterMap())
      .build();
    ApplicationContext.setCurrentRequest(requestContext);
    log.info("Request Log: {}", requestContext);
    return true;
  }

  protected abstract String prefix();

}
