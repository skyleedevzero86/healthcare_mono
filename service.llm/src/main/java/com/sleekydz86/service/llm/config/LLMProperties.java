package com.sleekydz86.service.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLMProperties {
    private boolean enabled = true;
    private String provider = "router";
    
    private RoutingProperties routing;
    private LlamaCppProperties llamaCpp;
    private Glm47Properties glm47;
    private KmBertProperties kmBert;
    private CacheProperties cache;
    private RateLimitProperties rateLimit;
}
