package com.sleekydz86.service.healthcare.exception;

import lombok.Getter;

@Getter
public class HealthDataProcessingException extends RuntimeException {
    private final String errorCode;
    private final boolean recoverable;
    private final Object context;

    public HealthDataProcessingException(String message) {
        super(message);
        this.errorCode = "HEALTH_DATA_ERROR";
        this.recoverable = false;
        this.context = null;
    }

    public HealthDataProcessingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.recoverable = false;
        this.context = null;
    }

    public HealthDataProcessingException(String message, String errorCode, boolean recoverable) {
        super(message);
        this.errorCode = errorCode;
        this.recoverable = recoverable;
        this.context = null;
    }

    public HealthDataProcessingException(String message, String errorCode, boolean recoverable, Object context) {
        super(message);
        this.errorCode = errorCode;
        this.recoverable = recoverable;
        this.context = context;
    }

    public HealthDataProcessingException(String message, Throwable cause, String errorCode, boolean recoverable) {
        super(message, cause);
        this.errorCode = errorCode;
        this.recoverable = recoverable;
        this.context = null;
    }
}

