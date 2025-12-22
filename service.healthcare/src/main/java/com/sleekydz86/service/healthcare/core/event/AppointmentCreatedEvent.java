package com.sleekydz86.service.healthcare.core.event;

import com.sleekydz86.service.healthcare.core.eventsourcing.DomainEvent;
import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentCreatedEvent implements DomainEvent {
    private final UUID eventId;
    private final LocalDateTime timestamp;
    private final String aggregateId;
    private final int version;
    private final String patientId;
    private final String doctorId;
    private final LocalDateTime appointmentDateTime;
    private final String reason;
    private final String notes;

    public AppointmentCreatedEvent(String aggregateId, String patientId, String doctorId,
                                  LocalDateTime appointmentDateTime, String reason, String notes) {
        this.eventId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.aggregateId = aggregateId;
        this.version = 1;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDateTime = appointmentDateTime;
        this.reason = reason;
        this.notes = notes;
    }

    @Override
    public UUID getEventId() { return eventId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public String getAggregateId() { return aggregateId; }
    @Override
    public String getEventType() { return "AppointmentCreated"; }
    @Override
    public int getVersion() { return version; }

    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public String getReason() { return reason; }
    public String getNotes() { return notes; }
}

