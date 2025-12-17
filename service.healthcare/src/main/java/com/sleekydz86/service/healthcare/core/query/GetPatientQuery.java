package com.sleekydz86.service.healthcare.core.query;

import com.sleekydz86.api.gateway.cqrs.query.Query;
import java.time.LocalDateTime;
import java.util.UUID;

public class GetPatientQuery implements Query {
    private final UUID queryId;
    private final LocalDateTime timestamp;
    private final String patientId;

    public GetPatientQuery(String patientId) {
        this.queryId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.patientId = patientId;
    }

    @Override
    public UUID getQueryId() { return queryId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public String getQueryType() { return "GetPatient"; }
    @Override
    public Object getParameters() { return patientId; }

    public String getPatientId() { return patientId; }
}

