package com.sleekydz86.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Patient {
    private Long patientId;
    private String name;
    private String regionId;
    private Integer shardKey;
    private String email;
    private String phone;
    private LocalDateTime dateOfBirth;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

