package com.sleekydz86.service.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.km-bert")
public class KmBertProperties {
    private boolean enabled = true;
    private String apiKey;
    private String baseUrl = "https://router.huggingface.co/hf-inference/models/madatnlp/km-bert";
    private int timeout = 10000;
    private int maxRetries = 3;
    private boolean useForPreprocessing = true;
}

