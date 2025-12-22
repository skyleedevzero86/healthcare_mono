package com.sleekydz86.service.healthcare.core.readmodel;

import java.time.LocalDateTime;

public class PatientReadModel {
    private String patientId;
    private String patientName;
    private String phoneNumber;
    private String email;
    private String address;
    private String medicalHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PatientReadModel() {
    }

    public PatientReadModel(String patientId, String patientName, String phoneNumber,
                           String email, String address, String medicalHistory,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.medicalHistory = medicalHistory;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

