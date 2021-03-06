package com.sixsprints.core.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestContext {

  private String requestId;

  private Long startTime;

  private String selfUrl;

  private String remoteAddress;

  private String localAddress;

  private String remoteHost;

  private String server;

  private String uri;

  private String queryString;

  private String userAgent;

  private String httpMethod;

  private Map<String, List<String>> headersMap;

  private Map<String, List<String>> parametersMap;

}
