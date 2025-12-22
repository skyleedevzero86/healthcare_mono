package com.sleekydz86.service.healthcare.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

