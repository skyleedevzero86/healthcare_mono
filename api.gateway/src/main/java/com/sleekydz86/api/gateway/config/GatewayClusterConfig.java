package com.sleekydz86.api.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayClusterConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("healthcare-service", r -> r.path("/api/healthcare/**")
                .filters(f -> f.circuitBreaker(c -> c.setName("healthcare-circuit")
                    .setFallbackUri("forward:/fallback/healthcare")))
                .uri("lb://service.healthcare"))
            .route("auth-service", r -> r.path("/api/auth/**")
                .filters(f -> f.circuitBreaker(c -> c.setName("auth-circuit")
                    .setFallbackUri("forward:/fallback/auth")))
                .uri("lb://service.auth"))
            .route("comm-service", r -> r.path("/api/comm/**")
                .filters(f -> f.circuitBreaker(c -> c.setName("comm-circuit")
                    .setFallbackUri("forward:/fallback/comm")))
                .uri("lb://service.comm"))
            .route("usermanagement-service", r -> r.path("/api/usermanagement/**")
                .filters(f -> f.circuitBreaker(c -> c.setName("usermanagement-circuit")
                    .setFallbackUri("forward:/fallback/usermanagement")))
                .uri("lb://service.usermanagement"))
            .build();
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}

