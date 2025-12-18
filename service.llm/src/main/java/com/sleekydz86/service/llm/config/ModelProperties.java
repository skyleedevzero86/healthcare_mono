package com.sleekydz86.service.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.llama-cpp.model")
public class ModelProperties {
    private String name = "qwen2.5-7b-instruct-q4_k_m";
    private int contextSize = 16384;
    private int maxTokens = 2048;
    private double temperature = 0.7;
    private double topP = 0.9;
    private int topK = 40;
    private double repeatPenalty = 1.1;
}

