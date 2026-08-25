package com.chanmorales.isp.service;

import com.chanmorales.isp.dto.RefreshTokenData;
import com.chanmorales.isp.exception.InvalidRefreshTokenException;
import com.chanmorales.isp.model.User;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class TokenService {

  private static final String REFRESH_KEY_PREFIX = "refresh_token:";
  private static final String USER_TOKENS_PREFIX = "user_refresh_tokens:";
  private static final String BLACKLIST_KEY_PREFIX = "blacklisted_jti:";

  private final RedisTemplate<String, Object> redisTemplate;
  private final SecureRandom secureRandom;
  private final ObjectMapper objectMapper;

  @Value("${jwt.refresh-expiration-ms}")
  private long refreshExpirationMs;

  public String createRefreshToken(User user) {
    // Revoke all tokens for user to enforce single active session
    // Remove this call if multi-device concurrent session will be supported
    revokeAllRefreshTokenForUser(user.getId());

    byte[] randomBytes = new byte[64];
    secureRandom.nextBytes(randomBytes);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

    Duration ttl = Duration.ofMillis(refreshExpirationMs);
    RefreshTokenData data = new RefreshTokenData(user.getId(), user.getUsername());

    redisTemplate.opsForValue().set(REFRESH_KEY_PREFIX + token, data, ttl);
    // Tracker for all user tokens
    redisTemplate.opsForSet().add(USER_TOKENS_PREFIX + user.getId(), token);
    redisTemplate.expire(USER_TOKENS_PREFIX + user.getId(), ttl);

    return token;
  }

  public RefreshTokenData verifyRefreshToken(String token) {
    RefreshTokenData data =
        objectMapper.convertValue(
            redisTemplate.opsForValue().get(REFRESH_KEY_PREFIX + token), RefreshTokenData.class);
    if (data == null) {
      throw new InvalidRefreshTokenException();
    }

    return data;
  }

  public void revokeRefreshToken(String token) {
    RefreshTokenData data =
        objectMapper.convertValue(
            redisTemplate.opsForValue().get(REFRESH_KEY_PREFIX + token), RefreshTokenData.class);
    redisTemplate.delete(REFRESH_KEY_PREFIX + token);
    if (data != null) {
      redisTemplate.opsForSet().remove(USER_TOKENS_PREFIX + data.userId(), token);
    }
  }

  public void blacklistAccessToken(String jti, Instant tokenExpiry) {
    long ttlMs = tokenExpiry.toEpochMilli() - System.currentTimeMillis();
    if (ttlMs <= 0) {
      // Already expired, nothing to blacklist
      return;
    }

    redisTemplate.opsForValue().set(BLACKLIST_KEY_PREFIX + jti, true, Duration.ofMillis(ttlMs));
  }

  public boolean isAccessTokenBlacklisted(String jti) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_KEY_PREFIX + jti));
  }

  private void revokeAllRefreshTokenForUser(Long userId) {
    Set<Object> tokens = redisTemplate.opsForSet().members(USER_TOKENS_PREFIX + userId);
    if (tokens == null || tokens.isEmpty()) {
      return;
    }

    tokens.forEach(token -> redisTemplate.delete(REFRESH_KEY_PREFIX + token));
    redisTemplate.delete(USER_TOKENS_PREFIX + userId);
  }
}
