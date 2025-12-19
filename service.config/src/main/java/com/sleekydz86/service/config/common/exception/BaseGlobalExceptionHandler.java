package com.sleekydz86.service.config.common.exception;

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
public abstract class BaseGlobalExceptionHandler<T, E, B extends RuntimeException> {

    protected abstract T createErrorResponse(E errorCode, String message);
    
    protected abstract ResponseEntity<T> wrapResponse(T response, HttpStatus status);
    
    protected abstract E getErrorCodeFromBusinessException(B businessException);
    
    protected abstract E getUnknownErrorCode();
    
    protected abstract E getParamValidErrorCode();
    
    protected abstract E getAuthErrorCode();
    
    protected abstract E getDuplicateKeyErrorCode();
    
    protected ResponseEntity<T> handleBusinessExceptionInternal(B e) {
        log.warn("비즈니스 예외 발생: code={}, message={}", 
                getErrorCodeFromBusinessException(e), e.getMessage());
        T response = createErrorResponse(getErrorCodeFromBusinessException(e), e.getMessage());
        return wrapResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<T> handleValidationExceptions(
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
        T response = createErrorResponse(getParamValidErrorCode(), userMessage);
        return wrapResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<T> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.warn("잘못된 인자: {}", ex.getMessage());
        T response = createErrorResponse(
                getParamValidErrorCode(), 
                "잘못된 요청 파라미터입니다: " + ex.getMessage()
        );
        return wrapResponse(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<T> handleNullPointerException(
            NullPointerException ex) {
        log.error("NullPointerException 발생 - 프로그래밍 오류 가능성", ex);
        String userMessage = "처리 중 오류가 발생했습니다. 관리자에게 문의해주세요.";
        T response = createErrorResponse(getUnknownErrorCode(), userMessage);
        return wrapResponse(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<T> handleDataAccessException(
            DataAccessException ex) {
        log.error("데이터베이스 접근 오류", ex);
        String userMessage = "데이터 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        T response = createErrorResponse(getUnknownErrorCode(), userMessage);
        return wrapResponse(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<T> handleDuplicateKeyException(
            DuplicateKeyException ex) {
        log.warn("중복 키 오류 발생", ex);
        T response = createErrorResponse(
                getDuplicateKeyErrorCode(), 
                "이미 존재하는 데이터입니다."
        );
        return wrapResponse(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<T> handleSpringAuthenticationException(
            org.springframework.security.core.AuthenticationException ex) {
        log.warn("인증 오류 발생: {}", ex.getMessage());
        T response = createErrorResponse(
                getAuthErrorCode(), 
                "인증이 필요합니다."
        );
        return wrapResponse(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<T> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex) {
        log.warn("권한 오류 발생: {}", ex.getMessage());
        T response = createErrorResponse(
                getAuthErrorCode(), 
                "접근 권한이 없습니다."
        );
        return wrapResponse(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<T> handleException(Exception ex) {
        log.error("예상치 못한 오류 발생", ex);
        String userMessage = "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        T response = createErrorResponse(getUnknownErrorCode(), userMessage);
        return wrapResponse(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

