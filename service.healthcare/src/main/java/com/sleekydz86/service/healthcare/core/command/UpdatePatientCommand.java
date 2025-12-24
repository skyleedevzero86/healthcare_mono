package com.sleekydz86.service.healthcare.core.command;

import com.sleekydz86.service.healthcare.core.cqrs.command.Command;
import java.time.LocalDateTime;
import java.util.UUID;

public class UpdatePatientCommand implements Command {
    private final UUID commandId;
    private final LocalDateTime timestamp;
    private final String aggregateId;
    private final String patientName;
    private final String phoneNumber;
    private final String email;
    private final String address;

    public UpdatePatientCommand(String aggregateId, String patientName, String phoneNumber,
                               String email, String address) {
        this.commandId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.aggregateId = aggregateId;
        this.patientName = patientName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    @Override
    public UUID getCommandId() { return commandId; }
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    @Override
    public String getAggregateId() { return aggregateId; }
    @Override
    public String getCommandType() { return "UpdatePatient"; }

    public String getPatientName() { return patientName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
}

