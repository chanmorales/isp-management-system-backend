package com.chanmorales.isp.service;

import com.chanmorales.isp.dto.CreateUserData;
import com.chanmorales.isp.dto.RefreshTokenData;
import com.chanmorales.isp.dto.request.LoginRequest;
import com.chanmorales.isp.dto.request.RefreshRequest;
import com.chanmorales.isp.dto.request.RegisterRequest;
import com.chanmorales.isp.dto.response.AuthResponse;
import com.chanmorales.isp.exception.InvalidCredentialsException;
import com.chanmorales.isp.model.Role;
import com.chanmorales.isp.model.User;
import com.chanmorales.isp.repository.UserRepository;
import com.chanmorales.isp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final JwtUtil jwtUtil;
  private final TokenService tokenService;

  public AuthResponse register(RegisterRequest request) {
    return issueToken(
        userService.createNewUser(
            new CreateUserData(
                request.username(), request.email(), request.password(), Role.USER)));
  }

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.username(), request.password()));
    User user =
        userRepository
            .findByUsername(request.username())
            .orElseThrow(InvalidCredentialsException::new);

    return issueToken(user);
  }

  public AuthResponse refresh(RefreshRequest request) {
    RefreshTokenData data = tokenService.verifyRefreshToken(request.refreshToken());

    // Rotation: old refresh token dies and a new pair is issued
    tokenService.revokeRefreshToken(request.refreshToken());

    User user =
        userRepository.findById(data.userId()).orElseThrow(InvalidCredentialsException::new);

    return issueToken(user);
  }

  public void logout(String accessToken, RefreshRequest request) {
    tokenService.revokeRefreshToken(request.refreshToken());

    // Aside from revoking the refresh token, blacklist the access token until ttl since it is still
    // usable
    String jti = jwtUtil.extractJti(accessToken);
    if (jti != null) {
      tokenService.blacklistAccessToken(jti, jwtUtil.extractExpiry(accessToken));
    }
  }

  private AuthResponse issueToken(User user) {
    String accessToken = jwtUtil.generateToken(user);
    String refreshToken = tokenService.createRefreshToken(user);
    return new AuthResponse(accessToken, refreshToken);
  }
}
