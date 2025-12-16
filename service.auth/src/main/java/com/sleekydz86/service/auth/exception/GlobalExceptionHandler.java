package com.sleekydz86.service.auth.exception;

import com.sleekydz86.service.auth.dto.ApiResponse;
import com.sleekydz86.service.auth.dto.ApiResultCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        log.warn("비즈니스 예외 발생: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> paramValidErr(MethodArgumentNotValidException exception) {
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
    public ResponseEntity<ApiResponse<Void>> duplicateKeyErr(DuplicateKeyException exception) {
        log.warn("중복 키 오류 발생");
        return ApiResponse.error(ApiResultCode.DUPLICATE_KEY_ERR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> authErr(AuthenticationException exception) {
        log.warn("인증 오류 발생");
        return ApiResponse.error(ApiResultCode.AUTH_ERR);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Void>> expJwtTokenErr(ExpiredJwtException exception) {
        log.warn("만료된 JWT 토큰");
        return ApiResponse.error(ApiResultCode.EXP_JWT_TOKEN_ERR);
    }

    @ExceptionHandler({MalformedJwtException.class, UnsupportedJwtException.class})
    public ResponseEntity<ApiResponse<Void>> invalidJwtTokenErr(Exception exception) {
        log.warn("잘못된 JWT 토큰");
        return ApiResponse.error(ApiResultCode.INVALID_JWT_TOKEN_ERR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> illegalArgumentErr(IllegalArgumentException exception) {
        log.warn("잘못된 인자: {}", exception.getMessage());
        return ApiResponse.error(ApiResultCode.PARAM_VALID_ERR);
    }

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(org.springframework.dao.DataAccessException ex) {
        log.error("데이터베이스 접근 오류", ex);
        String userMessage = "데이터 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        return ApiResponse.error(ApiResultCode.UNKOWN_ERR, userMessage);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointerException(NullPointerException ex) {
        log.error("NullPointerException 발생 - 프로그래밍 오류 가능성", ex);
        String userMessage = "처리 중 오류가 발생했습니다. 관리자에게 문의해주세요.";
        return ApiResponse.error(ApiResultCode.UNKOWN_ERR, userMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        log.error("예상치 못한 오류 발생", exception);
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error("5000", "내부 서버 오류가 발생했습니다."));
    }
}
