package com.chanmorales.isp.exception.response;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    String code, String message, Map<String, String> fieldErrors, Instant timestamp) {

  public ErrorResponse(String code, String message) {
    this(code, message, null, Instant.now());
  }

  public ErrorResponse(String code, String message, Map<String, String> fieldErrors) {
    this(code, message, fieldErrors, Instant.now());
  }
}
