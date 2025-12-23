package com.sleekydz86.service.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.llama-cpp.model")
public class LlamaCppModelProperties {
    private String name;
    private int contextSize = 16384;
    private int maxTokens = 2048;
    private double temperature = 0.7;
    private double topP = 0.9;
    private int topK = 40;
    private double repeatPenalty = 1.1;
}

