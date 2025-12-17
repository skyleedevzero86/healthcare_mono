package com.sleekydz86.api.gateway.saga;

import java.util.UUID;

public interface Saga {
    UUID getSagaId();
    String getSagaType();
    SagaStatus getStatus();
    void setStatus(SagaStatus status);
    Object getData();
    void setData(Object data);
}

