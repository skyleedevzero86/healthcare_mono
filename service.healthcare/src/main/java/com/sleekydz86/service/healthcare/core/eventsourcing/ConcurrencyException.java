package com.sleekydz86.service.healthcare.core.eventsourcing;

public class ConcurrencyException extends RuntimeException {
    public ConcurrencyException(String message) {
        super(message);
    }
}
