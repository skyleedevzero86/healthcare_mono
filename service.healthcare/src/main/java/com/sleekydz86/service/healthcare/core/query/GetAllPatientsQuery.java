package com.sleekydz86.service.healthcare.core.query;

import com.sleekydz86.api.gateway.cqrs.query.Query;
import java.time.LocalDateTime;
import java.util.UUID;

public class GetAllPatientsQuery implements Query {
    private final UUID queryId;
    private final LocalDateTime timestamp;
    private final int page;
    private final int size;

    public GetAllPatientsQuery(int page, int size) {
        this.queryId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.page = page;
        this.size = size;
    }

    @Override
    public UUID getQueryId() { return queryId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public String getQueryType() { return "GetAllPatients"; }
    @Override
    public Object getParameters() { return new Object[]{page, size}; }

    public int getPage() { return page; }
    public int getSize() { return size; }
}

