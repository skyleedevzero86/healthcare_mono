package com.sleekydz86.service.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm")
public class LLMProperties {
    private boolean enabled = true;
    private String provider = "llama-cpp";

    private LlamaCpp llamaCpp = new LlamaCpp();
    private Cache cache = new Cache();
    private RateLimit rateLimit = new RateLimit();

    @Data
    public static class LlamaCpp {
        private String serverUrl = "http://localhost:8080";
        private int timeout = 60000;
        private int maxRetries = 3;
        private Model model = new Model();
    }

    @Data
    public static class Model {
        private String name = "qwen2.5-7b-instruct-q4_k_m";
        private int contextSize = 16384;
        private int maxTokens = 2048;
        private double temperature = 0.7;
        private double topP = 0.9;
        private int topK = 40;
        private double repeatPenalty = 1.1;
    }

    @Data
    public static class Cache {
        private boolean enabled = true;
        private int ttl = 3600;
    }

    @Data
    public static class RateLimit {
        private boolean enabled = true;
        private int requestsPerMinute = 30;
        private int requestsPerHour = 500;
    }
}
