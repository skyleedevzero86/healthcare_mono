package com.sleekydz86.api.gateway.saga;

public enum SagaStatus {
    STARTED,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED
}

