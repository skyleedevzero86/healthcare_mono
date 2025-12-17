package com.sleekydz86.service.healthcare.service.sharding;

import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PatientShardingService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ShardingKeyGenerator shardingKeyGenerator;

    public Patient createPatient(Patient patient) {
        String regionId = patient.getRegionId();
        int shardKey = shardingKeyGenerator.generateShardKey(regionId);
        patient.setShardKey(shardKey);
        return patientRepository.save(patient);
    }

    public Patient findPatientById(Long patientId) {
        int shardKey = shardingKeyGenerator.generateShardKey(patientId);
        return patientRepository.findByPatientIdAndShardKey(patientId, shardKey);
    }

    public List<Patient> findPatientsByRegion(String regionId) {
        List<Patient> patients = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            patients.addAll(patientRepository.findByRegionIdAndShardKey(regionId, i));
        }
        return patients;
    }

    public Patient updatePatient(Patient patient) {
        if (patient.getPatientId() != null) {
            Patient existing = findPatientById(patient.getPatientId());
            if (existing != null) {
                if (patient.getName() != null) existing.setName(patient.getName());
                if (patient.getEmail() != null) existing.setEmail(patient.getEmail());
                if (patient.getPhone() != null) existing.setPhone(patient.getPhone());
                if (patient.getAddress() != null) existing.setAddress(patient.getAddress());
                if (patient.getDateOfBirth() != null) existing.setDateOfBirth(patient.getDateOfBirth());
                return patientRepository.save(existing);
            }
        }
        return createPatient(patient);
    }

    public void deletePatient(Long patientId) {
        Patient patient = findPatientById(patientId);
        if (patient != null) {
            patientRepository.delete(patient);
        }
    }
}

