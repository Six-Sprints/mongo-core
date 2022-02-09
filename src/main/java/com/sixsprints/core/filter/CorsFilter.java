package com.sixsprints.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sixsprints.core.dto.RequestContext;
import com.sixsprints.core.utils.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CorsFilter implements Filter {

  @Override
  public void destroy() {

  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException {
    Long startTime = System.currentTimeMillis();
    if (req instanceof HttpServletRequest && res instanceof HttpServletResponse) {
      HttpServletRequest request = (HttpServletRequest) req;
      HttpServletResponse response = (HttpServletResponse) res;
      String origin = request.getHeader(varyHeader());
      if (origin == null) {
        origin = "";
      }
      response.setHeader("Access-Control-Allow-Origin", allowOriginHeader(origin));
      response.setHeader("Vary", varyHeader());
      response.setHeader("Access-Control-Max-Age", maxAgeHeader());
      response.setHeader("Access-Control-Allow-Credentials", allowCredentialsHeader());
      response.setHeader("Access-Control-Allow-Methods", allowMethodsHeader());
      response.setHeader("Access-Control-Allow-Headers", allowedHeaders());
      addMoreHeaders(response);
    }
    chain.doFilter(req, res);
    logRequest(startTime);
  }

  protected void addMoreHeaders(HttpServletResponse response) {

  }

  protected void logRequest(Long startTime) {
    RequestContext request = ApplicationContext.getCurrentRequest();

    log.info("Response time taken for request {} {} {} {} milliseconds",
      request.getRequestId(), request.getHttpMethod(), request.getSelfUrl(),
      System.currentTimeMillis() - startTime);
  }

  protected String allowedHeaders() {
    return "Origin, X-Requested-With, Content-Type, Accept, X-CSRF-TOKEN, X-AUTH-TOKEN";
  }

  protected String allowMethodsHeader() {
    return "PUT, POST, GET, OPTIONS, DELETE";
  }

  protected String allowCredentialsHeader() {
    return "true";
  }

  protected String maxAgeHeader() {
    return "3600";
  }

  protected String varyHeader() {
    return "Origin";
  }

  protected String allowOriginHeader(String origin) {
    return origin;
  }

  @Override
  public void init(FilterConfig filterConfig) {
  }

}