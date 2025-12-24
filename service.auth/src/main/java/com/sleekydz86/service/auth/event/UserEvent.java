package com.sleekydz86.service.auth.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent implements Serializable {
    private String eventId;
    private String eventType;
    private String userId;
    private Object eventData;
    private LocalDateTime timestamp;
}

