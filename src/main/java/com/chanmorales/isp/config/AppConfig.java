package com.chanmorales.isp.config;

import java.security.SecureRandom;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public SecureRandom secureRandom() {
    return new SecureRandom();
  }
}
