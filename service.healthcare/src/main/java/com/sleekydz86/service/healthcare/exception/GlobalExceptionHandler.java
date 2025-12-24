package com.sleekydz86.service.healthcare.exception;

import com.sleekydz86.service.healthcare.dto.ApiResponse;
import com.sleekydz86.service.healthcare.dto.ApiResultCode;
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
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("비즈니스 예외 발생: code={}, message={}", 
                e.getErrorCode(), e.getMessage());
        return ApiResponse.<Void>error(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
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
        return ApiResponse.<Map<String, String>>error(ApiResultCode.PARAM_VALID_ERR, userMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.warn("잘못된 인자: {}", ex.getMessage());
        return ApiResponse.<Void>error(ApiResultCode.PARAM_VALID_ERR, 
                "잘못된 요청 파라미터입니다: " + ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointerException(
            NullPointerException ex) {
        log.error("NullPointerException 발생 - 프로그래밍 오류 가능성", ex);
        String userMessage = "처리 중 오류가 발생했습니다. 관리자에게 문의해주세요.";
        return ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR.code, userMessage);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(
            DataAccessException ex) {
        log.error("데이터베이스 접근 오류", ex);
        String userMessage = "데이터 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        return ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR.code, userMessage);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateKeyException(
            DuplicateKeyException ex) {
        log.warn("중복 키 오류 발생", ex);
        return ApiResponse.<Void>error(ApiResultCode.DUPLICATE_KEY_ERR, "이미 존재하는 데이터입니다.");
    }

    @ExceptionHandler(HealthDataProcessingException.class)
    public ResponseEntity<ApiResponse<Void>> handleHealthDataException(HealthDataProcessingException e) {
        log.error("건강 데이터 처리 오류 [{}]: {}", e.getErrorCode(), e.getMessage(), e);
        String userMessage = e.isRecoverable() 
            ? "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            : "데이터 처리 중 오류가 발생했습니다: " + e.getMessage();
        if (e.getContext() != null) {
            log.debug("에러 컨텍스트: {}", e.getContext());
        }
        return ApiResponse.<Void>error(ApiResultCode.INVALID_REQUEST.code, userMessage);
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex) {
        log.warn("인증 오류 발생: {}", ex.getMessage());
        return ApiResponse.<Void>error(ApiResultCode.AUTH_ERR, "인증이 필요합니다.");
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex) {
        log.warn("권한 오류 발생: {}", ex.getMessage());
        return ApiResponse.<Void>error(ApiResultCode.AUTH_ERR, "접근 권한이 없습니다.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("예상치 못한 오류 발생", ex);
        String userMessage = "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        return ApiResponse.<Void>error(ApiResultCode.UNKNOWN_ERR.code, userMessage);
    }
}

