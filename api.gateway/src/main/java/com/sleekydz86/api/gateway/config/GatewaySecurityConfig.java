package com.sleekydz86.api.gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GatewaySecurityConfig {

    @Bean
    public GlobalFilter requestSizeFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders headers = request.getHeaders();
            
            String contentLength = headers.getFirst(HttpHeaders.CONTENT_LENGTH);
            if (contentLength != null) {
                try {
                    long size = Long.parseLong(contentLength);
                    if (size > 10 * 1024 * 1024) {
                        return Mono.error(new IllegalArgumentException("요청 크기가 10MB를 초과합니다"));
                    }
                } catch (NumberFormatException e) {
                }
            }
            
            return chain.filter(exchange);
        };
    }
}

