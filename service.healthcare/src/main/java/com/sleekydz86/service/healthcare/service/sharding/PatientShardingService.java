package com.sleekydz86.service.healthcare.service.sharding;

import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.global.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientShardingService {

    private final PatientMapper patientMapper;
    private final ShardingKeyGenerator shardingKeyGenerator;

    public Patient createPatient(Patient patient) {
        String regionId = patient.getRegionId();
        int shardKey = shardingKeyGenerator.generateShardKey(regionId);
        patient.setShardKey(shardKey);
        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());
        patientMapper.insert(patient);
        return patient;
    }

    public Patient findPatientById(Long patientId) {
        int shardKey = shardingKeyGenerator.generateShardKey(patientId);
        return patientMapper.findByPatientIdAndShardKey(patientId, shardKey);
    }

    public List<Patient> findPatientsByRegion(String regionId) {
        List<Patient> patients = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            patients.addAll(patientMapper.findByRegionIdAndShardKey(regionId, i));
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
                existing.setUpdatedAt(LocalDateTime.now());
                patientMapper.update(existing);
                return existing;
            }
        }
        return createPatient(patient);
    }

    public void deletePatient(Long patientId) {
        patientMapper.deleteById(patientId);
    }
}

