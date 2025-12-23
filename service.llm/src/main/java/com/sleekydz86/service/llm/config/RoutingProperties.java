package com.sleekydz86.service.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.routing")
public class RoutingProperties {
    private String chat = "glm47";
    private String healthcare = "glm47";
    private String defaultProvider = "llama-cpp";
}

