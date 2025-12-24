package com.sleekydz86.service.commu.exception;

import com.sleekydz86.service.commu.dto.ApiResponse;
import com.sleekydz86.service.commu.dto.ApiResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.warn("비즈니스 예외 발생: code={}, message={}", 
                e.getErrorCode(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getErrorCode(), e.getMessage()).getBody());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        String errorDetails = errors.values().stream()
                .collect(Collectors.joining(", "));
        log.warn("입력 검증 실패: {}", errors);
        String userMessage = "입력값이 올바르지 않습니다: " + errorDetails;
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ApiResultCode.PARAM_VALID_ERR, userMessage).getBody());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.warn("잘못된 인자: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ApiResultCode.PARAM_VALID_ERR, 
                        "잘못된 요청 파라미터입니다: " + ex.getMessage()).getBody());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleNullPointerException(
            NullPointerException ex) {
        log.error("NullPointerException 발생 - 프로그래밍 오류 가능성", ex);
        String userMessage = "처리 중 오류가 발생했습니다. 관리자에게 문의해주세요.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ApiResultCode.UNKNOWN_ERR.code, userMessage).getBody());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<?>> handleDataAccessException(
            DataAccessException ex) {
        log.error("데이터베이스 접근 오류", ex);
        String userMessage = "데이터 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ApiResultCode.UNKNOWN_ERR.code, userMessage).getBody());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateKeyException(
            DuplicateKeyException ex) {
        log.warn("중복 키 오류 발생", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ApiResultCode.DUPLICATE_KEY_ERR, "이미 존재하는 데이터입니다.").getBody());
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex) {
        log.warn("인증 오류 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ApiResultCode.AUTH_ERR, "인증이 필요합니다.").getBody());
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex) {
        log.warn("권한 오류 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ApiResultCode.AUTH_ERR, "접근 권한이 없습니다.").getBody());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        log.error("예상치 못한 오류 발생", ex);
        String userMessage = "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ApiResultCode.UNKNOWN_ERR.code, userMessage).getBody());
    }
}

