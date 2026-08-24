package com.chanmorales.isp.exception;

public class FeatureDisabledException extends RuntimeException {
  public FeatureDisabledException() {
    super("This feature is currently unavailable.");
  }
}
