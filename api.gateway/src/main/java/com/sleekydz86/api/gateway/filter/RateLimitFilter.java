package com.sleekydz86.api.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.DefaultReactiveRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    
    private static final String RATE_LIMIT_SCRIPT = 
        "local key = KEYS[1]\n" +
        "local limit = tonumber(ARGV[1])\n" +
        "local window = tonumber(ARGV[2])\n" +
        "local current = redis.call('INCR', key)\n" +
        "if current == 1 then\n" +
        "    redis.call('EXPIRE', key, window)\n" +
        "end\n" +
        "if current > limit then\n" +
        "    return 0\n" +
        "end\n" +
        "return 1";

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/actuator/health",
        "/healthcare/v1/health_check"
    );

    private static final int DEFAULT_USER_LIMIT = 100;
    private static final int DEFAULT_IP_LIMIT = 50;
    private static final int WINDOW_SECONDS = 60;
    private static final int BURST_LIMIT = 200;

    public RateLimitFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        String clientId = getClientId(exchange);
        String userId = request.getHeaders().getFirst("X-User-Id");
        boolean isAuthenticated = userId != null && !userId.isEmpty();

        String userKey = "rate_limit:user:" + (isAuthenticated ? userId : clientId);
        String ipKey = "rate_limit:ip:" + clientId;

        int limit = isAuthenticated ? DEFAULT_USER_LIMIT : DEFAULT_IP_LIMIT;

        DefaultReactiveRedisScript<Long> script = new DefaultReactiveRedisScript<>();
        script.setScriptText(RATE_LIMIT_SCRIPT);
        script.setResultType(Long.class);

        return checkRateLimit(userKey, limit)
                .flatMap(userAllowed -> {
                    if (Boolean.FALSE.equals(userAllowed)) {
                        log.warn("사용자 요청 한도 초과: userId={}, clientId={}", userId, clientId);
                        return handleRateLimitExceeded(exchange);
                    }

                    if (!isAuthenticated) {
                        return checkRateLimit(ipKey, DEFAULT_IP_LIMIT)
                                .flatMap(ipAllowed -> {
                                    if (Boolean.FALSE.equals(ipAllowed)) {
                                        log.warn("IP 요청 한도 초과: clientId={}", clientId);
                                        return handleRateLimitExceeded(exchange);
                                    }
                                    return chain.filter(exchange);
                                });
                    }

                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    log.error("요청 한도 확인 실패", e);
                    return chain.filter(exchange);
                });
    }

    private Mono<Boolean> checkRateLimit(String key, int limit) {
        DefaultReactiveRedisScript<Long> script = new DefaultReactiveRedisScript<>();
        script.setScriptText(RATE_LIMIT_SCRIPT);
        script.setResultType(Long.class);

        List<String> keys = Collections.singletonList(key);
        List<String> args = Arrays.asList(String.valueOf(limit), String.valueOf(WINDOW_SECONDS));

        return redisTemplate.execute(script, keys, args.toArray())
                .map(result -> result != null && result == 1)
                .defaultIfEmpty(true)
                .onErrorReturn(true);
    }

    private String getClientId(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        
        String userId = request.getHeaders().getFirst("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            return "user:" + userId;
        }

        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            String[] ips = xForwardedFor.split(",");
            if (ips.length > 0) {
                return "ip:" + ips[0].trim();
            }
        }

        if (request.getRemoteAddress() != null) {
            return "ip:" + request.getRemoteAddress().getAddress().getHostAddress();
        }

        return "unknown";
    }

    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> handleRateLimitExceeded(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().add("Retry-After", String.valueOf(WINDOW_SECONDS));
        
        String errorResponse = "{\"resultCode\":\"4291\",\"resultMessage\":\"요청 한도를 초과했습니다. 잠시 후 다시 시도해주세요.\",\"resultData\":null}";
        DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -50;
    }
}

