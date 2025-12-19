package com.sleekydz86.service.healthcare.exception;

import com.sleekydz86.service.healthcare.dto.ApiResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String errorCode;
    private final ApiResultCode apiResultCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.apiResultCode = ApiResultCode.UNKNOWN_ERR;
    }

    public BusinessException(String message, ApiResultCode apiResultCode) {
        super(message);
        this.errorCode = apiResultCode.code;
        this.apiResultCode = apiResultCode;
    }

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.apiResultCode = ApiResultCode.UNKNOWN_ERR;
    }

    public BusinessException(String message, Throwable cause, ApiResultCode apiResultCode) {
        super(message, cause);
        this.errorCode = apiResultCode.code;
        this.apiResultCode = apiResultCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ApiResultCode getApiResultCode() {
        return apiResultCode;
    }
}

