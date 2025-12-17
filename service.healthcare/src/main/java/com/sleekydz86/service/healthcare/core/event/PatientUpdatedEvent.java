package com.sleekydz86.service.healthcare.core.event;

import com.sleekydz86.api.gateway.eventsourcing.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

public class PatientUpdatedEvent implements DomainEvent {
    private final UUID eventId;
    private final LocalDateTime timestamp;
    private final String aggregateId;
    private final int version;
    private final String patientName;
    private final String phoneNumber;
    private final String email;
    private final String address;

    public PatientUpdatedEvent(String aggregateId, String patientName, String phoneNumber,
                              String email, String address) {
        this.eventId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.aggregateId = aggregateId;
        this.version = 1;
        this.patientName = patientName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    @Override
    public UUID getEventId() { return eventId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public String getAggregateId() { return aggregateId; }
    @Override
    public String getEventType() { return "PatientUpdated"; }
    @Override
    public int getVersion() { return version; }

    public String getPatientName() { return patientName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
}

