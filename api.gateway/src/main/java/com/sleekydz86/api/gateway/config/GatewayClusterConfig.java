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
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.stripPrefix(0)
                                .circuitBreaker(c -> c.setName("authService")
                                        .setFallbackUri("forward:/fallback/auth")))
                        .uri("lb://service.auth")
                        .order(1))
                .route("healthcare-service", r -> r.path("/healthcare/**")
                        .filters(f -> f.stripPrefix(0)
                                .circuitBreaker(c -> c.setName("healthcareService")
                                        .setFallbackUri("forward:/fallback/healthcare")))
                        .uri("lb://service.healthcare")
                        .order(2))
                .route("community-service", r -> r.path("/community/**")
                        .filters(f -> f.stripPrefix(0)
                                .circuitBreaker(c -> c.setName("communityService")
                                        .setFallbackUri("forward:/fallback/community")))
                        .uri("lb://service.comm")
                        .order(3))
                .route("usermanagement-service", r -> r.path("/management/**")
                        .filters(f -> f.stripPrefix(0)
                                .circuitBreaker(c -> c.setName("usermanagementService")
                                        .setFallbackUri("forward:/fallback/management")))
                        .uri("lb://service.usermanagement")
                        .order(4))
                .route("llm-service", r -> r.path("/api/llm/**")
                        .filters(f -> f.stripPrefix(0)
                                .circuitBreaker(c -> c.setName("llmService")
                                        .setFallbackUri("forward:/fallback/llm")))
                        .uri("lb://service.llm")
                        .order(5))
                .build();
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
