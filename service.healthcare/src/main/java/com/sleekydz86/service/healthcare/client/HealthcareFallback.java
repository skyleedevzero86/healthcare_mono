package com.sleekydz86.service.healthcare.client;

import com.sleekydz86.service.healthcare.entity.MedicalRecord;
import com.sleekydz86.service.healthcare.entity.Patient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class HealthcareFallback implements HealthcareClient {

    @Override
    public Patient getPatient(Long id) {
        Patient patient = new Patient();
        patient.setPatientId(id);
        patient.setName("Service Unavailable");
        return patient;
    }

    @Override
    public Patient createPatient(Patient patient) {
        throw new RuntimeException("Healthcare service is temporarily unavailable");
    }

    @Override
    public List<MedicalRecord> getMedicalRecords(Long patientId) {
        return Collections.emptyList();
    }
}

