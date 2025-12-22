package com.sleekydz86.api.gateway.filter;

import com.sleekydz86.api.gateway.dto.ApiResultCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${token.secret:healthcare-secret-key-for-jwt-token-generation-minimum-256-bits}")
    private String secret;

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "token:blacklist:";
    private static final String TOKEN_USAGE_PREFIX = "token:usage:";

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/auth/v1/signin",
            "/auth/v1/signup",
            "/auth/v1/duplicateId",
            "/auth/v1/duplicateEmail",
            "/auth/v1/findUserId",
            "/auth/v1/findUserPw",
            "/healthcare/v1/health_check",
            "/actuator"
    );

    public AuthenticationFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isExcludedPath(path)) {
            return chain.filter(exchange);
        }

        String token = extractToken(request);

        if (!StringUtils.hasText(token)) {
            return handleUnauthorized(exchange, "토큰이 없습니다.");
        }

        if (token.length() > 2048) {
            log.warn("비정상적으로 긴 토큰: length={}", token.length());
            return handleUnauthorized(exchange, "유효하지 않은 토큰입니다.");
        }

        return checkTokenBlacklist(token)
                .flatMap(isBlacklisted -> {
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        log.warn("블랙리스트에 등록된 토큰 사용 시도");
                        return handleUnauthorized(exchange, "로그아웃된 토큰입니다.");
                    }

                    try {
                        if (validateToken(token)) {
                            Claims claims = parseClaims(token);
                            
                            if (claims.getExpiration() != null && claims.getExpiration().getTime() < System.currentTimeMillis()) {
                                log.warn("만료된 토큰 사용 시도");
                                return handleUnauthorized(exchange, "만료된 토큰입니다.");
                            }

                            String userId = claims.get("id", String.class);
                            String userRole = claims.get("role", String.class);
                            String source = claims.get("source", String.class);

                            if (!StringUtils.hasText(userId) || !StringUtils.hasText(userRole) || !StringUtils.hasText(source)) {
                                return handleUnauthorized(exchange, "유효하지 않은 토큰입니다.");
                            }

                            return recordTokenUsage(token, userId)
                                    .then(Mono.defer(() -> {
                                        ServerHttpRequest modifiedRequest = request.mutate()
                                                .header("X-User-Id", userId)
                                                .header("X-User-Role", userRole)
                                                .header("X-User-Source", source)
                                                .build();

                                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                                    }));
                        } else {
                            return handleUnauthorized(exchange, "유효하지 않은 토큰입니다.");
                        }
                    } catch (ExpiredJwtException e) {
                        log.warn("만료된 JWT 토큰: {}", e.getMessage());
                        return handleUnauthorized(exchange, "만료된 토큰입니다.");
                    } catch (MalformedJwtException | UnsupportedJwtException e) {
                        log.warn("잘못된 JWT 토큰: {}", e.getMessage());
                        return handleUnauthorized(exchange, "잘못된 토큰 형식입니다.");
                    } catch (Exception e) {
                        log.error("JWT 토큰 검증 중 오류 발생", e);
                        return handleUnauthorized(exchange, "토큰 검증 중 오류가 발생했습니다.");
                    }
                })
                .onErrorResume(e -> {
                    log.error("토큰 검증 처리 중 오류", e);
                    return handleUnauthorized(exchange, "토큰 검증 중 오류가 발생했습니다.");
                });
    }

    private Mono<Boolean> checkTokenBlacklist(String token) {
        String key = BLACKLIST_PREFIX + token;
        return redisTemplate.hasKey(key)
                .defaultIfEmpty(false)
                .onErrorReturn(false);
    }

    private Mono<Boolean> recordTokenUsage(String token, String userId) {
        String key = TOKEN_USAGE_PREFIX + token;
        return redisTemplate.opsForValue().set(key, userId, java.time.Duration.ofMinutes(5))
                .thenReturn(true)
                .onErrorReturn(false);
    }

    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7).trim();
            if (token.length() > 0 && token.length() <= 2048) {
                return token;
            }
        }
        return null;
    }

    private boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }

            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (claims.get("id") == null || "".equals(claims.get("id"))) {
                return false;
            }
            if (claims.get("role") == null || "".equals(claims.get("role"))) {
                return false;
            }
            if (claims.get("source") == null || "".equals(claims.get("source"))) {
                return false;
            }

            if (claims.getExpiration() != null && claims.getExpiration().getTime() < System.currentTimeMillis()) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorResponse = String.format(
                "{\"resultCode\":\"%s\",\"resultMessage\":\"%s\",\"resultData\":null}",
                ApiResultCode.UNAUTHORIZED.code,
                message
        );

        DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}

