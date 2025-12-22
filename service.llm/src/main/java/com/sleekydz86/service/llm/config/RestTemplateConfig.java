package com.sleekydz86.service.llm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, LLMProperties llmProperties) {
        return builder
                .setConnectTimeout(Duration.ofMillis(llmProperties.getLlamaCpp().getTimeout()))
                .setReadTimeout(Duration.ofMillis(llmProperties.getLlamaCpp().getTimeout()))
                .build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(LLMProperties llmProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) llmProperties.getLlamaCpp().getTimeout());
        factory.setReadTimeout((int) llmProperties.getLlamaCpp().getTimeout());
        return factory;
    }
}
