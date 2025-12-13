package com.sleekydz86.service.commu.exception;

import com.sleekydz86.service.commu.domain.dto.ApiResponse;
import com.sleekydz86.service.commu.domain.dto.ApiResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("잘못된 인자: {}", ex.getMessage());
        return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse> handleNullPointerException(NullPointerException ex) {
        log.error("NullPointerException 발생", ex);
        return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        log.error("예외 발생: {}", ex.getClass().getSimpleName(), ex);
        return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
    }
}

