package com.chanmorales.isp.security;

import com.chanmorales.isp.exception.ErrorResponseWriter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ErrorResponseWriter writer;

  @Override
  public void commence(
      @Nonnull HttpServletRequest request,
      @Nonnull HttpServletResponse response,
      @Nonnull AuthenticationException authException)
      throws IOException {
    var errorDetails = determineError(authException);
    writer.write(
        response, errorDetails.httpStatus, errorDetails.errorCode, errorDetails.errorMessage);
  }

  public void commence(HttpServletResponse response, JwtException jwtException) throws IOException {
    var errorDetails = determineError(jwtException);
    writer.write(
        response, errorDetails.httpStatus, errorDetails.errorCode, errorDetails.errorMessage);
  }

  private ErrorDetails determineError(AuthenticationException authException) {
    return switch (authException) {
      case BadCredentialsException _ ->
          new ErrorDetails(
              HttpStatus.UNAUTHORIZED, "AUTH_INVALID_CREDENTIALS", "Invalid username or password.");

      case DisabledException _ ->
          new ErrorDetails(HttpStatus.FORBIDDEN, "AUTH_ACCOUNT_DISABLED", "Account is disabled.");

      case LockedException _ ->
          new ErrorDetails(HttpStatus.FORBIDDEN, "AUTH_ACCOUNT_LOCKED", "Account is locked.");

      case AccountExpiredException _, CredentialsExpiredException _ ->
          new ErrorDetails(
              HttpStatus.FORBIDDEN, "AUTH_ACCOUNT_EXPIRED", "Account or credentials expired.");

      case AuthenticationException _ ->
          new ErrorDetails(
              HttpStatus.UNAUTHORIZED, "AUTHENTICATION_FAILED", "Authentication failed.");
    };
  }

  private ErrorDetails determineError(JwtException jwtException) {
    return switch (jwtException) {
      case SignatureException _, MalformedJwtException _, UnsupportedJwtException _ ->
          new ErrorDetails(
              HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_INVALID", "Authentication token is invalid.");

      case ExpiredJwtException _ ->
          new ErrorDetails(
              HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_EXPIRED", "Authentication token has expired.");

      case JwtException _ ->
          new ErrorDetails(
              HttpStatus.UNAUTHORIZED, "AUTH_TOKEN_INVALID", "Authentication token is invalid.");
    };
  }

  private record ErrorDetails(HttpStatus httpStatus, String errorCode, String errorMessage) {}
}
