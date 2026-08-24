package com.chanmorales.isp.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  JsonMapperBuilderCustomizer jsonCustomizer() {
    return builder ->
        builder.changeDefaultPropertyInclusion(value -> value.withValueInclusion(Include.NON_NULL));
  }
}
