package com.sleekydz86.service.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.glm47.thinking")
public class Glm47Thinking {
    private boolean enabled = true;
    private boolean preserved = true;
}

