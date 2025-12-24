package com.sleekydz86.service.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.glm47.chat.options")
public class Glm47ChatOptions {
    private double temperature = 1.0;
    private double topP = 0.95;
    private int maxTokens = 131072;
}

