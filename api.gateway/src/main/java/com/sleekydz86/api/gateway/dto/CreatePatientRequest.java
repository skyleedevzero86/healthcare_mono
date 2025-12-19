package com.sleekydz86.api.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientRequest {
    private String patientId;
    private String patientName;
    private String phoneNumber;
    private String email;
    private String address;
    private String medicalHistory;
}

