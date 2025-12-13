package com.sleekydz86.api.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SensitiveDataLoggingFilter implements GlobalFilter, Ordered {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(\"password\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern TOKEN_PATTERN = Pattern.compile("(\"token\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern AUTHORIZATION_PATTERN = Pattern.compile("(Authorization\\s*:\\s*Bearer\\s+)([^\\s]+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("(\"email\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\"tel\"\\s*:\\s*\")([^\"]+)(\")", Pattern.CASE_INSENSITIVE);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        
        log.info("요청 경로: {}", path);
        log.info("요청 메서드: {}", request.getMethod());
        
        String headers = sanitizeHeaders(request.getHeaders().toString());
        log.debug("요청 헤더: {}", headers);
        
        ServerHttpResponse response = exchange.getResponse();
        DataBufferFactory bufferFactory = response.bufferFactory();
        
        ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(org.reactivestreams.Publisher<? extends DataBuffer> body) {
                if (body instanceof Mono) {
                    return ((Mono<? extends DataBuffer>) body).flatMap(dataBuffer -> {
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        DataBufferUtils.release(dataBuffer);
                        
                        String responseBody = new String(content, StandardCharsets.UTF_8);
                        String sanitized = sanitizeResponse(responseBody);
                        
                        if (sanitized.length() > 500) {
                            log.debug("응답 본문 (일부): {}", sanitized.substring(0, 500) + "...");
                        } else {
                            log.debug("응답 본문: {}", sanitized);
                        }
                        
                        DataBuffer buffer = bufferFactory.wrap(content);
                        return getDelegate().writeWith(Mono.just(buffer));
                    });
                }
                return super.writeWith(body);
            }
        };

        return chain.filter(exchange.mutate().response(responseDecorator).build());
    }

    private String sanitizeHeaders(String headers) {
        String sanitized = AUTHORIZATION_PATTERN.matcher(headers).replaceAll("$1***");
        return sanitized;
    }

    private String sanitizeResponse(String response) {
        String sanitized = response;
        sanitized = PASSWORD_PATTERN.matcher(sanitized).replaceAll("$1***$3");
        sanitized = TOKEN_PATTERN.matcher(sanitized).replaceAll("$1***$3");
        sanitized = EMAIL_PATTERN.matcher(sanitized).replaceAll("$1***$3");
        sanitized = PHONE_PATTERN.matcher(sanitized).replaceAll("$1***$3");
        return sanitized;
    }

    @Override
    public int getOrder() {
        return -150;
    }
}

