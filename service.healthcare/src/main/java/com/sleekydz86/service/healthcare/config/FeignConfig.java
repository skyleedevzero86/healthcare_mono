package com.sleekydz86.service.healthcare.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.sleekydz86.service.healthcare.client")
public class FeignConfig {
}

