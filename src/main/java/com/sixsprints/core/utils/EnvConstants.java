package com.sixsprints.core.utils;

public interface EnvConstants {

  String TOKEN_SECRET = System.getenv().getOrDefault("JWT_TOKEN_SECRET", "c3azG3fkA3f1GdsL");

  String ISSUER = System.getenv().getOrDefault("JWT_ISSUER", "https://www.website.com");

  byte[] SHARED_SECRET = System.getenv().getOrDefault("JWT_SHARED_SECRET", "f0jtXAnEq6bdLdjaV91CrXRsXu6oyvof").getBytes();
  
  Integer TOKEN_EXPIRY_IN_DAYS = Integer.parseInt(System.getenv().getOrDefault("JWT_TOKEN_EXPIRY", "30"));
  
}
