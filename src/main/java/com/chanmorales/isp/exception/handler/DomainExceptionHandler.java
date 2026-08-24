package com.chanmorales.isp.exception.handler;

import com.chanmorales.isp.exception.FeatureDisabledException;
import com.chanmorales.isp.exception.InvalidCredentialsException;
import com.chanmorales.isp.exception.InvalidRefreshTokenException;
import com.chanmorales.isp.exception.ResourceAlreadyExistsException;
import com.chanmorales.isp.exception.ResourceNotFoundException;
import com.chanmorales.isp.exception.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DomainExceptionHandler {

  @ExceptionHandler(InvalidRefreshTokenException.class)
  public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse("AUTH_INVALID_REFRESH_TOKEN", ex.getMessage()));
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ErrorResponse("AUTH_INVALID_CREDENTIALS", ex.getMessage()));
  }

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleConflict(ResourceAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse("RESOURCE_CONFLICT", ex.getMessage()));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage()));
  }

  @ExceptionHandler(FeatureDisabledException.class)
  public ResponseEntity<ErrorResponse> handleDisabledFeature(FeatureDisabledException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse("FEATURE_DISABLED", ex.getMessage()));
  }
}
