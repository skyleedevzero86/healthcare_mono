package com.sleekydz86.service.healthcare.core.command;

import com.sleekydz86.api.gateway.cqrs.command.Command;
import java.time.LocalDateTime;
import java.util.UUID;

public class CreateAppointmentCommand implements Command {
    private final UUID commandId;
    private final LocalDateTime timestamp;
    private final String aggregateId;
    private final String appointmentId;
    private final String patientId;
    private final String doctorId;
    private final LocalDateTime appointmentDateTime;
    private final String reason;
    private final String notes;

    public CreateAppointmentCommand(String appointmentId, String patientId, String doctorId,
                                   LocalDateTime appointmentDateTime, String reason, String notes) {
        this.commandId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.aggregateId = appointmentId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDateTime = appointmentDateTime;
        this.reason = reason;
        this.notes = notes;
    }

    @Override
    public UUID getCommandId() { return commandId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public String getAggregateId() { return aggregateId; }
    @Override
    public String getCommandType() { return "CreateAppointment"; }

    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public String getReason() { return reason; }
    public String getNotes() { return notes; }
}

