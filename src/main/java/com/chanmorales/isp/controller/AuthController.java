package com.chanmorales.isp.controller;

import com.chanmorales.isp.common.FeatureProperties;
import com.chanmorales.isp.dto.request.LoginRequest;
import com.chanmorales.isp.dto.request.RefreshRequest;
import com.chanmorales.isp.dto.request.RegisterRequest;
import com.chanmorales.isp.dto.response.AuthResponse;
import com.chanmorales.isp.exception.FeatureDisabledException;
import com.chanmorales.isp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final FeatureProperties featureProperties;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    if (!featureProperties.registerEndpoint().enabled()) {
      throw new FeatureDisabledException();
    }

    return ResponseEntity.ok(authService.register(request));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
    return ResponseEntity.ok(authService.refresh(request));
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void logout(
      @RequestHeader("Authorization") String authHeader,
      @Valid @RequestBody RefreshRequest request) {
    String accessToken = authHeader.replace("Bearer ", "");
    authService.logout(accessToken, request);
  }
}
