package com.sleekydz86.service.healthcare.core.event;

import com.sleekydz86.service.healthcare.core.eventsourcing.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

public class MedicalRecordAddedEvent implements DomainEvent {
    private final UUID eventId;
    private final LocalDateTime timestamp;
    private final String aggregateId;
    private final int version;
    private final String record;

    public MedicalRecordAddedEvent(String aggregateId, String record) {
        this.eventId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.aggregateId = aggregateId;
        this.version = 1;
        this.record = record;
    }

    @Override
    public UUID getEventId() { return eventId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public String getAggregateId() { return aggregateId; }
    @Override
    public String getEventType() { return "MedicalRecordAdded"; }
    @Override
    public int getVersion() { return version; }

    public String getRecord() { return record; }
}

