package com.chanmorales.isp.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException() {
    super("User not found.");
  }
}
