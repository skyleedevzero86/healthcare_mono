package com.sleekydz86.service.healthcare.service.cache;

import com.sleekydz86.service.healthcare.entity.MedicalRecord;
import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.repository.MedicalRecordRepository;
import com.sleekydz86.service.healthcare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Cacheable(value = "patients", key = "#patientId", unless = "#result == null")
    public Patient getPatient(Long patientId) {
        return patientRepository.findById(patientId).orElse(null);
    }

    @CacheEvict(value = "patients", key = "#patient.patientId")
    public Patient updatePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    @CacheEvict(value = "patients", allEntries = true)
    public void clearPatientCache() {
    }

    @Cacheable(value = "medical-records", key = "#patientId", unless = "#result.isEmpty()")
    public List<MedicalRecord> getMedicalRecords(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }

    public void cachePatientData(Patient patient) {
        String key = "patient:" + patient.getPatientId();
        redisTemplate.opsForValue().set(key, patient, Duration.ofHours(24));
    }

    public Patient getCachedPatient(Long patientId) {
        String key = "patient:" + patientId;
        return (Patient) redisTemplate.opsForValue().get(key);
    }
}

