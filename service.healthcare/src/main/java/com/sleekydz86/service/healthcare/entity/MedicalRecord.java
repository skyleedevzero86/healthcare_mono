package com.sleekydz86.service.healthcare.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    private Long recordId;
    private Long patientId;
    private LocalDateTime visitDate;
    private String doctorName;
    private String diagnosis;
    private String treatment;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

