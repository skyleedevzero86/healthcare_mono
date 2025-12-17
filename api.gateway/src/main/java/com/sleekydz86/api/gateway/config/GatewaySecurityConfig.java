package com.sleekydz86.api.gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf().disable()
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .authorizeExchange()
                .pathMatchers("/auth/v1/signin", "/auth/v1/signup").permitAll()
                .pathMatchers("/healthcare/**", "/management/**", "/community/**").authenticated()
                .anyExchange().authenticated()
            .and()
            .oauth2ResourceServer()
                .jwt()
            .and()
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

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

