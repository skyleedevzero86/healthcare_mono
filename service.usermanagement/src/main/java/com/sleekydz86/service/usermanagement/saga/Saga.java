package com.sleekydz86.service.usermanagement.saga;

import java.util.UUID;

public interface Saga {
    UUID getSagaId();
    String getSagaType();
    SagaStatus getStatus();
    void setStatus(SagaStatus status);
    Object getData();
    void setData(Object data);
}

