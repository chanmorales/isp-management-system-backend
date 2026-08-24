package com.chanmorales.isp.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "features")
public record FeatureProperties(RegisterEndpoint registerEndpoint) {
  public record RegisterEndpoint(boolean enabled) {}
}
