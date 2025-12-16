package com.sleekydz86.api.gateway.exception;

import com.sleekydz86.api.gateway.dto.ApiResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
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
        String message = "서버 오류가 발생했습니다.";
        String code = ApiResultCode.UNKOWN_ERR.code;

        if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            status = HttpStatus.resolve(responseStatusException.getStatusCode().value());
            message = responseStatusException.getReason();
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            message = "잘못된 요청입니다: " + ex.getMessage();
            code = ApiResultCode.INVALID_REQUEST.code;
        } else if (ex instanceof IllegalStateException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "서비스를 사용할 수 없습니다: " + ex.getMessage();
            code = ApiResultCode.SERVICE_UNAVAILABLE.code;
        }

        log.error("API Gateway 예외 발생: {}", ex.getMessage(), ex);

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

