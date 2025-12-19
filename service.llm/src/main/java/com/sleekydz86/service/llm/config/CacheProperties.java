package com.sleekydz86.service.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.cache")
public class CacheProperties {
    private boolean enabled = true;
    private int ttl = 3600;
}

