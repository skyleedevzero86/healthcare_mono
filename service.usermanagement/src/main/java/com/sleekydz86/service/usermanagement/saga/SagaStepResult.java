package com.sleekydz86.service.usermanagement.saga;

public class SagaStepResult {
    private final boolean success;
    private final String message;
    private final Object data;

    public SagaStepResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
}

