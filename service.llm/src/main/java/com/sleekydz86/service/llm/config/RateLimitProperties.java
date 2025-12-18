package com.sleekydz86.service.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.rate-limit")
public class RateLimitProperties {
    private boolean enabled = true;
    private int requestsPerMinute = 30;
    private int requestsPerHour = 500;
}

