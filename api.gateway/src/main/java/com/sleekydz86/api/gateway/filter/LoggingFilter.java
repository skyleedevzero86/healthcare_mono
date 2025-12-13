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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getURI().getPath();
        String requestMethod = request.getMethod() != null ? request.getMethod().name() : "알 수 없음";
        String requestId = request.getId();

        log.info("=== API Gateway 요청 시작 ===");
        log.info("요청 ID: {}", requestId);
        log.info("요청 시간: {}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        log.info("요청 메서드: {}", requestMethod);
        log.info("요청 경로: {}", requestPath);
        log.info("요청 헤더: {}", request.getHeaders());
        log.info("요청 쿼리: {}", request.getURI().getQuery());

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
                        
                        log.info("=== API Gateway 응답 ===");
                        log.info("요청 ID: {}", requestId);
                        log.info("응답 시간: {}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        log.info("응답 상태: {}", getStatusCode());
                        log.info("응답 헤더: {}", getHeaders());
                        if (responseBody.length() > 1000) {
                            log.info("응답 본문 (일부): {}", responseBody.substring(0, 1000) + "...");
                        } else {
                            log.info("응답 본문: {}", responseBody);
                        }
                        log.info("=== API Gateway 요청 종료 ===");
                        
                        DataBuffer buffer = bufferFactory.wrap(content);
                        return getDelegate().writeWith(Mono.just(buffer));
                    });
                }
                return super.writeWith(body);
            }
        };

        return chain.filter(exchange.mutate().response(responseDecorator).build());
    }

    @Override
    public int getOrder() {
        return -200;
    }
}

