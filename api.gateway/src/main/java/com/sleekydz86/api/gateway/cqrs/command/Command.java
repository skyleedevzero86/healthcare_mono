package com.sleekydz86.api.gateway.cqrs.command;

import java.time.LocalDateTime;
import java.util.UUID;

public interface Command {
    UUID getCommandId();
    LocalDateTime getTimestamp();
    String getAggregateId();
    String getCommandType();
}

