package com.sleekydz86.api.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j

@Component
public class CorsFilter implements GlobalFilter, Ordered {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8981,http://localhost:19006}")
    private String allowedOrigins;

    private static final List<String> ALLOWED_METHODS = Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
    );

    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
        "Content-Type", "Authorization", "X-Requested-With", "X-User-Id", "X-User-Role", "X-User-Source"
    );

    private static final List<String> EXPOSED_HEADERS = Arrays.asList(
        "X-User-Id", "X-User-Role", "X-User-Source"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        if (CorsUtils.isCorsRequest(request)) {
            HttpHeaders headers = response.getHeaders();
            
            String origin = request.getHeaders().getFirst(HttpHeaders.ORIGIN);
            if (origin != null && isAllowedOrigin(origin)) {
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            } else if (origin != null) {
                log.warn("허용되지 않은 Origin: {}", origin);
            }
            
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(", ", ALLOWED_METHODS));
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, String.join(", ", ALLOWED_HEADERS));
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, String.join(", ", EXPOSED_HEADERS));
            headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");

            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return response.setComplete();
            }
        }

        return chain.filter(exchange);
    }

    private boolean isAllowedOrigin(String origin) {
        if (origin == null || origin.isEmpty()) {
            return false;
        }

        List<String> allowed = Arrays.asList(allowedOrigins.split(","));
        return allowed.stream().anyMatch(allowedOrigin -> {
            String trimmed = allowedOrigin.trim();
            if (trimmed.endsWith("*")) {
                String prefix = trimmed.substring(0, trimmed.length() - 1);
                return origin.startsWith(prefix);
            }
            return origin.equals(trimmed);
        });
    }

    @Override
    public int getOrder() {
        return -300;
    }
}

