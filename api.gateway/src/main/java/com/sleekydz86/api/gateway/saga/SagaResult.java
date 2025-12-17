package com.sleekydz86.api.gateway.saga;

public class SagaResult {
    private final boolean success;
    private final String message;
    private final Saga saga;

    public SagaResult(boolean success, String message, Saga saga) {
        this.success = success;
        this.message = message;
        this.saga = saga;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Saga getSaga() { return saga; }
}

