package com.sleekydz86.api.gateway.saga;

public class PatientRegistrationData {
    private String patientId;
    private String patientName;
    private String phoneNumber;
    private String email;
    private String address;
    private String medicalHistory;
    private String userId;
    private String notificationSent;
    private String userAccountCreated;

    public PatientRegistrationData() {
    }

    public PatientRegistrationData(String patientId, String patientName, String phoneNumber,
                                  String email, String address, String medicalHistory) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.medicalHistory = medicalHistory;
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
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getNotificationSent() { return notificationSent; }
    public void setNotificationSent(String notificationSent) { this.notificationSent = notificationSent; }
    public String getUserAccountCreated() { return userAccountCreated; }
    public void setUserAccountCreated(String userAccountCreated) { this.userAccountCreated = userAccountCreated; }
}

