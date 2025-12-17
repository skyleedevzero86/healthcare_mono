package com.sleekydz86.service.healthcare.core.query;

import com.sleekydz86.api.gateway.cqrs.query.Query;
import java.time.LocalDateTime;
import java.util.UUID;

public class GetAppointmentsByPatientQuery implements Query {
    private final UUID queryId;
    private final LocalDateTime timestamp;
    private final String patientId;
    private final LocalDateTime fromDate;
    private final LocalDateTime toDate;

    public GetAppointmentsByPatientQuery(String patientId, LocalDateTime fromDate, LocalDateTime toDate) {
        this.queryId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.patientId = patientId;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public UUID getQueryId() { return queryId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public String getQueryType() { return "GetAppointmentsByPatient"; }
    @Override
    public Object getParameters() { return new Object[]{patientId, fromDate, toDate}; }

    public String getPatientId() { return patientId; }
    public LocalDateTime getFromDate() { return fromDate; }
    public LocalDateTime getToDate() { return toDate; }
}

