package com.sleekydz86.service.healthcare.core.cqrs.query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Query {
    UUID getQueryId();
    LocalDateTime getTimestamp();
    String getQueryType();
    Object getParameters();
}

