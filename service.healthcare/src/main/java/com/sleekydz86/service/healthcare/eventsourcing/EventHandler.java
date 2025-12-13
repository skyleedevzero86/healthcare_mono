package com.sleekydz86.service.healthcare.eventsourcing;

import com.sleekydz86.service.healthcare.event.HealthDataEvent;

public interface EventHandler {
    void handle(HealthDataEvent event);
}

