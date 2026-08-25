package com.chanmorales.isp.exception;

public class InvalidCredentialsException extends RuntimeException {

  public InvalidCredentialsException() {
    super("Invalid username or password.");
  }
}
