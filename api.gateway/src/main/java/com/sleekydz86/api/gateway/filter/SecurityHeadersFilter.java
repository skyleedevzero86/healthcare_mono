package com.sleekydz86.api.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        
        if (!headers.containsKey("X-Content-Type-Options")) {
            headers.add("X-Content-Type-Options", "nosniff");
        }
        
        if (!headers.containsKey("X-Frame-Options")) {
            headers.add("X-Frame-Options", "DENY");
        }
        
        if (!headers.containsKey("X-XSS-Protection")) {
            headers.add("X-XSS-Protection", "1; mode=block");
        }
        
        if (!headers.containsKey("Strict-Transport-Security")) {
            headers.add("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        }
        
        if (!headers.containsKey("Content-Security-Policy")) {
            headers.add("Content-Security-Policy", 
                "default-src 'self'; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                "style-src 'self' 'unsafe-inline'; " +
                "img-src 'self' data: https:; " +
                "font-src 'self' data:; " +
                "connect-src 'self'; " +
                "frame-ancestors 'none'; " +
                "base-uri 'self'; " +
                "form-action 'self'");
        }
        
        if (!headers.containsKey("Referrer-Policy")) {
            headers.add("Referrer-Policy", "strict-origin-when-cross-origin");
        }
        
        if (!headers.containsKey("Permissions-Policy")) {
            headers.add("Permissions-Policy", 
                "geolocation=(), " +
                "microphone=(), " +
                "camera=(), " +
                "payment=(), " +
                "usb=(), " +
                "magnetometer=(), " +
                "gyroscope=(), " +
                "accelerometer=()");
        }
        
        headers.add("X-Permitted-Cross-Domain-Policies", "none");
        headers.add("Cross-Origin-Embedder-Policy", "require-corp");
        headers.add("Cross-Origin-Opener-Policy", "same-origin");
        headers.add("Cross-Origin-Resource-Policy", "same-origin");
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -400;
    }
}

