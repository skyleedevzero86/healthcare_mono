package com.sleekydz86.service.auth.exception;

import com.sleekydz86.service.auth.dto.ApiResponse;
import com.sleekydz86.service.auth.dto.ApiResultCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> paramValidErr(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("입력 검증 실패: {}", errors);
        return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse> duplicateKeyErr(DuplicateKeyException exception) {
        log.warn("중복 키 오류 발생");
        return ApiResponse.error(ApiResultCode.DUPLICATE_KEY_ERR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> authErr(AuthenticationException exception) {
        log.warn("인증 오류 발생");
        return ApiResponse.error(ApiResultCode.AUTH_ERR);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse> expJwtTokenErr(ExpiredJwtException exception) {
        log.warn("만료된 JWT 토큰");
        return ApiResponse.error(ApiResultCode.EXP_JWT_TOKEN_ERR);
    }

    @ExceptionHandler({MalformedJwtException.class, UnsupportedJwtException.class})
    public ResponseEntity<ApiResponse> invalidJwtTokenErr(Exception exception) {
        log.warn("잘못된 JWT 토큰");
        return ApiResponse.error(ApiResultCode.INVALID_JWT_TOKEN_ERR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> illegalArgumentErr(IllegalArgumentException exception) {
        log.warn("잘못된 인자: {}", exception.getMessage());
        return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception exception) {
        log.error("예외 발생: {}", exception.getClass().getSimpleName(), exception);
        return ApiResponse.error(ApiResultCode.UNKOWN_ERR);
    }
}
