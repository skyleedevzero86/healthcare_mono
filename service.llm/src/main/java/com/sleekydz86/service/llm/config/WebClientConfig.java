package com.sleekydz86.service.llm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Slf4j
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(LlamaCppProperties llamaCppProperties,
                               Glm47Properties glm47Properties,
                               KmBertProperties kmBertProperties) {
        int maxTimeout = Math.max(
            Math.max(llamaCppProperties.getTimeout(), glm47Properties.getTimeout()),
            kmBertProperties.getTimeout()
        );
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(maxTimeout))
                .option(reactor.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, maxTimeout);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
