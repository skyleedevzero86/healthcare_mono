package com.sleekydz86.service.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.glm47")
public class Glm47Properties {
    private boolean enabled = true;
    private String apiKey;
    private String baseUrl = "https://api.z.ai/api/paas/v4";
    private String model = "glm-4.7";
    private int timeout = 120000;
    private int maxRetries = 3;
    private Glm47ChatOptions chat = new Glm47ChatOptions();
    private Glm47Thinking thinking = new Glm47Thinking();
}

