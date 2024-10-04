package com.sixsprints.core.interceptor;

import java.util.Date;

import org.slf4j.MDC;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import com.sixsprints.core.dto.RequestContext;
import com.sixsprints.core.utils.ApplicationContext;
import com.sixsprints.core.utils.HttpRequestUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractRequestContextInterceptor implements AsyncHandlerInterceptor {

  private static final String REQUEST_ID = "request";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse,
    Object handler) {
    RequestContext requestContext = RequestContext.builder()
      .requestId(HttpRequestUtil.getRequestId(prefix()))
      .startTime(new Date().getTime())
      .selfUrl(HttpRequestUtil.getSelfUrl(request))
      .remoteAddress(HttpRequestUtil.getRemoteAddress(request))
      .localAddress(request.getLocalAddr())
      .remoteHost(request.getRemoteHost())
      .server(HttpRequestUtil.getServer(request))
      .uri(request.getRequestURI())
      .queryString(request.getQueryString())
      .userAgent(request.getHeader("User-Agent"))
      .httpMethod(request.getMethod())
      .headersMap(HttpRequestUtil.getHeadersMap(request))
      .parametersMap(HttpRequestUtil.getParamsMap(request))
      .build();
    ApplicationContext.setCurrentRequest(requestContext);
    MDC.put(REQUEST_ID, requestContext.getRequestId());
    postProcessor(requestContext);
    return true;
  }

  protected void postProcessor(RequestContext requestContext) {
    if (requestContext.getUserAgent() == null || !requestContext.getUserAgent().contains("kube-probe")) {
      log.info("Request Log : {}", requestContext);
    }
  }

  protected abstract String prefix();

}
