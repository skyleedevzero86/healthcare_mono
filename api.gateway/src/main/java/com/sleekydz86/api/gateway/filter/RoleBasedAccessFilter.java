package com.sleekydz86.api.gateway.filter;

import com.sleekydz86.api.gateway.dto.ApiResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class RoleBasedAccessFilter implements GlobalFilter, Ordered {

    public RoleBasedAccessFilter() {
    }

    private static final Set<String> PUBLIC_PATHS = new HashSet<>(Arrays.asList(
        "/auth/v1/signin",
        "/auth/v1/signup",
        "/auth/v1/duplicateId",
        "/auth/v1/duplicateEmail",
        "/auth/v1/findUserId",
        "/auth/v1/findUserPw",
        "/healthcare/v1/health_check",
        "/actuator"
    ));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String userRole = request.getHeaders().getFirst("X-User-Role");
        if (userRole == null || userRole.isEmpty()) {
            log.warn("역할 정보가 없는 요청: {}", path);
            return handleForbidden(exchange, "접근 권한이 없습니다.");
        }

        if (!hasBasicAccess(userRole, path)) {
            log.warn("접근 거부: role={}, path={}", userRole, path);
            return handleForbidden(exchange, "해당 리소스에 대한 접근 권한이 없습니다.");
        }

        return chain.filter(exchange);
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    private boolean hasBasicAccess(String role, String path) {
        if ("ROLE_ADMIN".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            return true;
        }

        if ("ROLE_DOCTOR".equalsIgnoreCase(role) || "DOCTOR".equalsIgnoreCase(role)) {
            return !path.startsWith("/management/v1/delete") && 
                   !path.startsWith("/management/v1/manage_userList");
        }

        if ("ROLE_GUARDIAN".equalsIgnoreCase(role) || "GUARDIAN".equalsIgnoreCase(role)) {
            return path.startsWith("/healthcare/v1/") || 
                   path.startsWith("/management/v1/userInfo");
        }

        if ("ROLE_PATIENT".equalsIgnoreCase(role) || "PATIENT".equalsIgnoreCase(role)) {
            return path.startsWith("/healthcare/v1/") || 
                   path.startsWith("/community/v1/");
        }

        return false;
    }

    private Mono<Void> handleForbidden(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorResponse = String.format(
            "{\"resultCode\":\"%s\",\"resultMessage\":\"%s\",\"resultData\":null}",
            ApiResultCode.FORBIDDEN.code,
            message
        );

        DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -90;
    }
}

