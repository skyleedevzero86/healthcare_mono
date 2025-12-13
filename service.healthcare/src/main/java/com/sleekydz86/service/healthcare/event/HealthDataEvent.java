package com.sleekydz86.service.healthcare.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataEvent implements Serializable {
    private String eventId;
    private String eventType;
    private String userId;
    private String dataType;
    private Object eventData;
    private LocalDateTime timestamp;
    private String source;
}

