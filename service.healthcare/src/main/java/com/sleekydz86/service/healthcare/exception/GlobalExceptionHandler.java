package com.sleekydz86.service.healthcare.exception;

import com.sleekydz86.service.healthcare.dto.ApiResponse;
import com.sleekydz86.service.healthcare.dto.ApiResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("입력 검증 실패: {}", errors);
        return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("잘못된 인자: {}", ex.getMessage());
        return ApiResponse.error(ApiResultCode.INVALID_REQUEST);
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

