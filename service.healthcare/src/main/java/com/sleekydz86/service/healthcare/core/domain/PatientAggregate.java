package com.sleekydz86.service.healthcare.core.domain;

import com.sleekydz86.api.gateway.eventsourcing.DomainEvent;
import com.sleekydz86.service.healthcare.core.event.MedicalRecordAddedEvent;
import com.sleekydz86.service.healthcare.core.event.PatientCreatedEvent;
import com.sleekydz86.service.healthcare.core.event.PatientUpdatedEvent;

import java.util.ArrayList;
import java.util.List;

public class PatientAggregate {
    private String patientId;
    private String patientName;
    private String phoneNumber;
    private String email;
    private String address;
    private String medicalHistory;
    private int version;
    private List<DomainEvent> uncommittedEvents;

    public PatientAggregate(String patientId, String patientName, String phoneNumber,
                           String email, String address, String medicalHistory) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.medicalHistory = medicalHistory;
        this.version = 0;
        this.uncommittedEvents = new ArrayList<>();

        PatientCreatedEvent event = new PatientCreatedEvent(
            patientId, patientName, phoneNumber, email, address, medicalHistory);
        applyEvent(event);
    }

    public void updatePatient(String patientName, String phoneNumber, String email, String address) {
        PatientUpdatedEvent event = new PatientUpdatedEvent(
            patientId, patientName, phoneNumber, email, address);
        applyEvent(event);
    }

    public void addMedicalRecord(String record) {
        MedicalRecordAddedEvent event = new MedicalRecordAddedEvent(patientId, record);
        applyEvent(event);
    }

    private void applyEvent(DomainEvent event) {
        uncommittedEvents.add(event);
        version++;

        if (event instanceof PatientCreatedEvent) {
            PatientCreatedEvent e = (PatientCreatedEvent) event;
            this.patientName = e.getPatientName();
            this.phoneNumber = e.getPhoneNumber();
            this.email = e.getEmail();
            this.address = e.getAddress();
            this.medicalHistory = e.getMedicalHistory();
        } else if (event instanceof PatientUpdatedEvent) {
            PatientUpdatedEvent e = (PatientUpdatedEvent) event;
            this.patientName = e.getPatientName();
            this.phoneNumber = e.getPhoneNumber();
            this.email = e.getEmail();
            this.address = e.getAddress();
        } else if (event instanceof MedicalRecordAddedEvent) {
            MedicalRecordAddedEvent e = (MedicalRecordAddedEvent) event;
            this.medicalHistory += "\n" + e.getRecord();
        }
    }

    public List<DomainEvent> getUncommittedEvents() {
        return new ArrayList<>(uncommittedEvents);
    }

    public void markEventsAsCommitted() {
        uncommittedEvents.clear();
    }

    public String getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getMedicalHistory() { return medicalHistory; }
    public int getVersion() { return version; }
}

