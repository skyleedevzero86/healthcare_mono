package com.sleekydz86.service.healthcare.core.eventsourcing;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
    UUID getEventId();
    LocalDateTime getTimestamp();
    String getAggregateId();
    String getEventType();
    int getVersion();
}

