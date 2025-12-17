package com.sleekydz86.service.commu.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private Long userId;
    private String message;
    private String type;
}

