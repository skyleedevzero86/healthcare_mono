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
    public RestTemplate restTemplate(RestTemplateBuilder builder, 
                                     LlamaCppProperties llamaCppProperties,
                                     Glm47Properties glm47Properties,
                                     KmBertProperties kmBertProperties) {
        int maxTimeout = Math.max(
            Math.max(llamaCppProperties.getTimeout(), glm47Properties.getTimeout()),
            kmBertProperties.getTimeout()
        );
        return builder
                .setConnectTimeout(Duration.ofMillis(maxTimeout))
                .setReadTimeout(Duration.ofMillis(maxTimeout))
                .build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(LlamaCppProperties llamaCppProperties,
                                                              Glm47Properties glm47Properties,
                                                              KmBertProperties kmBertProperties) {
        int maxTimeout = Math.max(
            Math.max(llamaCppProperties.getTimeout(), glm47Properties.getTimeout()),
            kmBertProperties.getTimeout()
        );
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(maxTimeout);
        factory.setReadTimeout(maxTimeout);
        return factory;
    }
}
