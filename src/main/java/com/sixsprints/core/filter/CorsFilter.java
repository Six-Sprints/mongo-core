package com.sixsprints.core.filter;

import java.io.IOException;

import com.sixsprints.core.dto.RequestContext;
import com.sixsprints.core.utils.ApplicationContext;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
      response.setHeader("Access-Control-Allow-Origin", allowOriginHeader(request));
      response.setHeader("Vary", varyHeader(request));
      response.setHeader("Access-Control-Max-Age", maxAgeHeader(request));
      response.setHeader("Access-Control-Allow-Credentials", allowCredentialsHeader(request));
      response.setHeader("Access-Control-Allow-Methods", allowMethodsHeader(request));
      response.setHeader("Access-Control-Allow-Headers", allowedHeaders(request));
      addMoreHeaders(response);
    }
    chain.doFilter(req, res);
    logRequest(startTime);
  }

  protected String fetchOriginFromRequest(HttpServletRequest request) {
    return fetchHeaderFromRequest(request, "Origin");
  }

  protected String allowedHeaders(HttpServletRequest request) {
    return "Origin, X-Requested-With, Content-Type, Accept, X-CSRF-TOKEN, X-AUTH-TOKEN";
  }

  protected String allowMethodsHeader(HttpServletRequest request) {
    return "PUT, POST, GET, OPTIONS, DELETE";
  }

  protected String allowCredentialsHeader(HttpServletRequest request) {
    return "true";
  }

  protected String maxAgeHeader(HttpServletRequest request) {
    return "3600";
  }

  protected String varyHeader(HttpServletRequest request) {
    return "Origin";
  }

  protected void addMoreHeaders(HttpServletResponse response) {

  }

  protected String fetchHeaderFromRequest(HttpServletRequest request, String headerName) {
    String header = request.getHeader(headerName);
    if (header == null) {
      header = "";
    }
    return header;
  }

  protected String allowOriginHeader(HttpServletRequest request) {
    return fetchOriginFromRequest(request);
  }

  protected void logRequest(Long startTime) {
    RequestContext request = ApplicationContext.getCurrentRequest();
    log.info("Response time taken for request {} {} {} {} milliseconds",
      request.getRequestId(), request.getHttpMethod(), request.getSelfUrl(),
      System.currentTimeMillis() - startTime);
  }

  @Override
  public void init(FilterConfig filterConfig) {
  }

}