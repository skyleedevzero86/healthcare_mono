package com.sleekydz86.service.healthcare.core.eventsourcing;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventEntity {
    private Long id;
    private UUID eventId;
    private String aggregateId;
    private String eventType;
    private int version;
    private LocalDateTime timestamp;
    private String eventData;

    public EventEntity() {
    }

    public EventEntity(UUID eventId, String aggregateId, String eventType, int version, LocalDateTime timestamp, String eventData) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.version = version;
        this.timestamp = timestamp;
        this.eventData = eventData;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getEventData() { return eventData; }
    public void setEventData(String eventData) { this.eventData = eventData; }
}

