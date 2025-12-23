package com.sleekydz86.service.llm.config;

import com.sleekydz86.service.llm.infrastructure.adapter.llm.Glm47Provider;
import com.sleekydz86.service.llm.infrastructure.adapter.llm.LlamaCppProvider;
import com.sleekydz86.service.llm.infrastructure.adapter.llm.LLMProviderRouter;
import com.sleekydz86.service.llm.ports.outbound.LLMProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class LLMProviderConfig {

    @Bean
    @ConditionalOnProperty(name = "llm.provider", havingValue = "router", matchIfMissing = true)
    @Primary
    public LLMProvider llmProviderRouter(
            LlamaCppProvider llamaCppProvider,
            Glm47Provider glm47Provider,
            RoutingProperties routingProperties) {
        return new LLMProviderRouter(llamaCppProvider, glm47Provider, routingProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "llm.provider", havingValue = "llama-cpp")
    public LLMProvider llamaCppProviderOnly(LlamaCppProvider llamaCppProvider) {
        return llamaCppProvider;
    }

    @Bean
    @ConditionalOnProperty(name = "llm.provider", havingValue = "glm47")
    public LLMProvider glm47ProviderOnly(Glm47Provider glm47Provider) {
        return glm47Provider;
    }
}

