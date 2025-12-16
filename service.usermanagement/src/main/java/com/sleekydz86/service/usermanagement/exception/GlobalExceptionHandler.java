package com.sleekydz86.service.usermanagement.exception;

import com.sleekydz86.service.usermanagement.dto.ApiResponse;
import com.sleekydz86.service.usermanagement.dto.ApiResultCode;
import lombok.extern.slf4j.Slf4j;
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

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("비즈니스 예외 발생: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
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
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("잘못된 인자: {}", ex.getMessage());
        return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointerException(NullPointerException ex) {
        log.error("NullPointerException 발생 - 프로그래밍 오류 가능성", ex);
        String userMessage = "처리 중 오류가 발생했습니다. 관리자에게 문의해주세요.";
        return ApiResponse.error(ApiResultCode.UNKOWN_ERR.code, userMessage);
    }

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(org.springframework.dao.DataAccessException ex) {
        log.error("데이터베이스 접근 오류", ex);
        String userMessage = "데이터 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        return ApiResponse.error(ApiResultCode.UNKOWN_ERR.code, userMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        log.error("예상치 못한 오류 발생", exception);
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error("5000", "내부 서버 오류가 발생했습니다."));
    }
}

