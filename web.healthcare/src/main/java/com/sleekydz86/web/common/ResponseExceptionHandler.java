package com.sleekydz86.web.common;

import com.sleekydz86.web.global.exception.BusinessException;
import com.sleekydz86.web.global.exception.ResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ResponseExceptionHandler {
    private static final Map<HttpStatus, String> ERROR_MAP = new HashMap<>();

    static {
        ERROR_MAP.put(HttpStatus.CONFLICT, "중복이 있습니다.");
        ERROR_MAP.put(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        ERROR_MAP.put(HttpStatus.NOT_ACCEPTABLE, "지정된 형식이 아닙니다.");
    }

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        log.warn("비즈니스 예외 발생: {}", e.getMessage());
        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(ResponseException.class)
    public ApiResponse<String> handleResponseException(ResponseException e) {
        return ApiResponse.of(e.getStatus(), ERROR_MAP.get(e.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        log.error("예상치 못한 오류 발생", e);
        return ApiResponse.error("5000", "내부 서버 오류가 발생했습니다.");
    }
}
