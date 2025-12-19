package com.sleekydz86.service.config.common.exception;

import com.sleekydz86.service.config.common.dto.ApiResponse;
import com.sleekydz86.service.config.common.dto.ApiResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ConfigGlobalExceptionHandler 
    extends BaseGlobalExceptionHandler<
        ApiResponse<Void>,
        ApiResultCode,
        BusinessException
    > {

    @Override
    protected ApiResponse<Void> createErrorResponse(ApiResultCode errorCode, String message) {
        return ApiResponse.<Void>builder()
                .code(errorCode.code)
                .message(message)
                .data(null)
                .timestamp(java.time.LocalDateTime.now())
                .build();
    }

    @Override
    protected ResponseEntity<ApiResponse<Void>> wrapResponse(
            ApiResponse<Void> response, HttpStatus status) {
        return ResponseEntity.status(status).body(response);
    }

    @Override
    protected ApiResultCode getErrorCodeFromBusinessException(BusinessException e) {
        return e.getApiResultCode();
    }

    @Override
    protected ApiResultCode getUnknownErrorCode() {
        return ApiResultCode.UNKNOWN_ERR;
    }

    @Override
    protected ApiResultCode getParamValidErrorCode() {
        return ApiResultCode.PARAM_VALID_ERR;
    }

    @Override
    protected ApiResultCode getAuthErrorCode() {
        return ApiResultCode.AUTH_ERR;
    }

    @Override
    protected ApiResultCode getDuplicateKeyErrorCode() {
        return ApiResultCode.DUPLICATE_KEY_ERR;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        return handleBusinessExceptionInternal(e);
    }
}

