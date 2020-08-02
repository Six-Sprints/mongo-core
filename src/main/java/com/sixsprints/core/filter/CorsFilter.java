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

import org.apache.catalina.connector.RequestFacade;
import org.springframework.stereotype.Component;

import com.sixsprints.core.utils.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Component
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
      String origin = request.getHeader("Origin");
      if (origin == null) {
        origin = "";
      }
      response.setHeader("Access-Control-Allow-Origin", origin);
      response.setHeader("Vary", "Origin");
      response.setHeader("Access-Control-Max-Age", "3600");
      response.setHeader("Access-Control-Allow-Credentials", "true");
      response.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, OPTIONS, DELETE");
      response.setHeader("Access-Control-Allow-Headers",
        "Origin, X-Requested-With, Content-Type, Accept, X-CSRF-TOKEN, X-AUTH-TOKEN");
    }
    chain.doFilter(req, res);
    RequestFacade request = (RequestFacade) req;
    log.info("Response time taken for request {} {} {} {} milliseconds",
      ApplicationContext.getCurrentRequest().getRequestId(), request.getMethod(), request.getRequestURI(),
      System.currentTimeMillis() - startTime);
  }

  @Override
  public void init(FilterConfig filterConfig) {
  }

}