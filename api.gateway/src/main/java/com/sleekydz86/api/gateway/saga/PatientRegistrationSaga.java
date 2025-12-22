package com.sleekydz86.api.gateway.saga;

import java.util.UUID;

public class PatientRegistrationSaga implements Saga {
    private final UUID sagaId;
    private final String sagaType;
    private SagaStatus status;
    private PatientRegistrationData data;

    public PatientRegistrationSaga(PatientRegistrationData data) {
        this.sagaId = UUID.randomUUID();
        this.sagaType = "PatientRegistration";
        this.status = SagaStatus.STARTED;
        this.data = data;
    }

    @Override
    public UUID getSagaId() { return sagaId; }
    @Override
    public String getSagaType() { return sagaType; }
    @Override
    public SagaStatus getStatus() { return status; }
    @Override
    public void setStatus(SagaStatus status) { this.status = status; }
    @Override
    public Object getData() { return data; }
    @Override
    public void setData(Object data) { this.data = (PatientRegistrationData) data; }
}

