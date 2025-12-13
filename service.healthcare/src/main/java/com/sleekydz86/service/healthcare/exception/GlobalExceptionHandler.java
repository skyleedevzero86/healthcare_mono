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
        
        String errorDetails = String.join(", ", errors.values());
        log.warn("입력 검증 실패: {}", errors);
        
        String userMessage = "입력값이 올바르지 않습니다: " + errorDetails;
        
        return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR, userMessage);
    }

    @ExceptionHandler(HealthDataProcessingException.class)
    public ResponseEntity<ApiResponse> handleHealthDataException(HealthDataProcessingException e) {
        log.error("건강 데이터 처리 오류 [{}]: {}", e.getErrorCode(), e.getMessage(), e);
        
        String userMessage = e.isRecoverable() 
            ? "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            : "데이터 처리 중 오류가 발생했습니다: " + e.getMessage();
        
        if (e.getContext() != null) {
            log.debug("에러 컨텍스트: {}", e.getContext());
        }
        
        HttpStatus status = e.isRecoverable() ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.BAD_REQUEST;
        
        return ResponseEntity.status(status)
            .body(ApiResponse.error(ApiResultCode.INVALID_REQUEST, userMessage));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("잘못된 인자: {}", ex.getMessage());
        String userMessage = "요청 파라미터가 올바르지 않습니다: " + ex.getMessage();
        return ApiResponse.error(ApiResultCode.INVALID_REQUEST, userMessage);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse> handleNullPointerException(NullPointerException ex) {
        log.error("NullPointerException 발생 - 프로그래밍 오류 가능성", ex);
        String userMessage = "처리 중 오류가 발생했습니다. 관리자에게 문의해주세요.";
        return ApiResponse.error(ApiResultCode.UNKOWN_ERR, userMessage);
    }

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<ApiResponse> handleDataAccessException(org.springframework.dao.DataAccessException ex) {
        log.error("데이터베이스 접근 오류", ex);
        String userMessage = "데이터 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        return ApiResponse.error(ApiResultCode.UNKOWN_ERR, userMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        log.error("예상치 못한 예외 발생: {}", ex.getClass().getSimpleName(), ex);
        
        String userMessage = "처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        
        log.error("예외 상세 정보 - 클래스: {}, 메시지: {}, 스택: {}", 
            ex.getClass().getName(), 
            ex.getMessage(),
            ex.getStackTrace().length > 0 ? ex.getStackTrace()[0] : "스택 없음");
        
        return ApiResponse.error(ApiResultCode.UNKOWN_ERR, userMessage);
    }
}

