package com.chanmorales.isp.common;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FeatureProperties.class)
public class FeatureConfig {}
