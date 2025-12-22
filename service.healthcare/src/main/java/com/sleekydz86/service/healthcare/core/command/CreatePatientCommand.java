package com.sleekydz86.service.healthcare.core.command;

import com.sleekydz86.api.gateway.cqrs.command.Command;
import java.time.LocalDateTime;
import java.util.UUID;

public class CreatePatientCommand implements Command {
    private final UUID commandId;
    private final LocalDateTime timestamp;
    private final String aggregateId;
    private final String patientName;
    private final String patientId;
    private final String phoneNumber;
    private final String email;
    private final String address;
    private final String medicalHistory;

    public CreatePatientCommand(String patientId, String patientName, String phoneNumber,
                               String email, String address, String medicalHistory) {
        this.commandId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.aggregateId = patientId;
        this.patientName = patientName;
        this.patientId = patientId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.medicalHistory = medicalHistory;
    }

    @Override
    public UUID getCommandId() { return commandId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public String getAggregateId() { return aggregateId; }
    @Override
    public String getCommandType() { return "CreatePatient"; }

    public String getPatientName() { return patientName; }
    public String getPatientId() { return patientId; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getMedicalHistory() { return medicalHistory; }
}

