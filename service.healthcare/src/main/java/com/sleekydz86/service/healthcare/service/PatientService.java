package com.sleekydz86.service.healthcare.service;

import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.repository.PatientRepository;
import com.sleekydz86.service.healthcare.service.cache.CacheService;
import com.sleekydz86.service.healthcare.service.sharding.PatientShardingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientShardingService shardingService;

    @Autowired
    private CacheService cacheService;

    public Patient createPatient(Patient patient) {
        Patient createdPatient = shardingService.createPatient(patient);
        cacheService.cachePatientData(createdPatient);
        return createdPatient;
    }

    public Patient getPatient(Long id) {
        Patient patient = cacheService.getCachedPatient(id);
        if (patient != null) {
            return patient;
        }

        patient = shardingService.findPatientById(id);
        if (patient != null) {
            cacheService.cachePatientData(patient);
        }

        return patient;
    }

    public Patient updatePatient(Patient patient) {
        Patient updatedPatient = patientRepository.save(patient);
        cacheService.cachePatientData(updatedPatient);
        return updatedPatient;
    }
}

