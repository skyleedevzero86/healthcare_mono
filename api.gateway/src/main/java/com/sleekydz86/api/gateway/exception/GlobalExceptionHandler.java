package com.sleekydz86.api.gateway.exception;

import com.sleekydz86.api.gateway.dto.ApiResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        String code = ApiResultCode.UNKOWN_ERR.code;

        if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            status = HttpStatus.resolve(responseStatusException.getStatusCode().value());
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            message = responseStatusException.getReason() != null 
                ? responseStatusException.getReason() 
                : "요청 처리 중 오류가 발생했습니다.";
            code = ApiResultCode.INVALID_REQUEST.code;
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            message = "잘못된 요청 파라미터입니다: " + ex.getMessage();
            code = ApiResultCode.INVALID_REQUEST.code;
            log.warn("잘못된 인자: {}", ex.getMessage());
        } else if (ex instanceof IllegalStateException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "서비스를 사용할 수 없습니다. 잠시 후 다시 시도해주세요.";
            code = ApiResultCode.SERVICE_UNAVAILABLE.code;
            log.warn("서비스 사용 불가: {}", ex.getMessage());
        } else if (ex instanceof NullPointerException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "처리 중 오류가 발생했습니다. 관리자에게 문의해주세요.";
            code = ApiResultCode.UNKOWN_ERR.code;
            log.error("NullPointerException 발생 - 프로그래밍 오류 가능성", ex);
        } else if (ex instanceof DataAccessException) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "데이터 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
            code = ApiResultCode.UNKOWN_ERR.code;
            log.error("데이터베이스 접근 오류", ex);
        } else if (ex instanceof DuplicateKeyException) {
            status = HttpStatus.CONFLICT;
            message = "이미 존재하는 데이터입니다.";
            code = ApiResultCode.INVALID_REQUEST.code;
            log.warn("중복 키 오류 발생", ex);
        } else if (ex instanceof AuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "인증이 필요합니다.";
            code = ApiResultCode.UNAUTHORIZED.code;
            log.warn("인증 오류 발생: {}", ex.getMessage());
        } else if (ex instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
            message = "접근 권한이 없습니다.";
            code = ApiResultCode.FORBIDDEN.code;
            log.warn("권한 오류 발생: {}", ex.getMessage());
        } else {
            log.error("예상치 못한 오류 발생", ex);
        }

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorResponse = String.format(
                "{\"resultCode\":\"%s\",\"resultMessage\":\"%s\",\"resultData\":null,\"timestamp\":\"%s\"}",
                code,
                message,
                java.time.LocalDateTime.now()
        );

        DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}

