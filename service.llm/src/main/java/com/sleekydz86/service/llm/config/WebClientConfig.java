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
    public WebClient webClient(LLMProperties llmProperties) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(llmProperties.getLlamaCpp().getTimeout()))
                .option(reactor.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        (int) llmProperties.getLlamaCpp().getTimeout());

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
