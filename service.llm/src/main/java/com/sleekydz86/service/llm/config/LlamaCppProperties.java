package com.sleekydz86.service.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.llama-cpp")
public class LlamaCppProperties {
    private String serverUrl = "http://localhost:8080";
    private int timeout = 60000;
    private int maxRetries = 3;
}

