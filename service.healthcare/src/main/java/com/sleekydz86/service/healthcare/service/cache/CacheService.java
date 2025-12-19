package com.sleekydz86.service.healthcare.service.cache;

import com.sleekydz86.service.healthcare.config.PerformanceConfig;
import com.sleekydz86.service.healthcare.entity.MedicalRecord;
import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.repository.MedicalRecordRepository;
import com.sleekydz86.service.healthcare.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CacheService {

    private static final String PATIENT_KEY_PREFIX = "patient:";
    private static final Duration PATIENT_DIRECT_TTL = PerformanceConfig.PATIENTS_TTL;

    private final RedisTemplate<String, Object> redisTemplate;
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Cacheable(value = PerformanceConfig.CACHE_PATIENTS, key = "#patientId", unless = "#result == null")
    public Patient getPatient(Long patientId) {
        return patientRepository.findById(patientId).orElse(null);
    }

    @CacheEvict(value = PerformanceConfig.CACHE_PATIENTS, key = "#patient.patientId")
    public Patient updatePatient(Patient patient) {
        Patient updated = patientRepository.save(patient);
        evictPatientDirectCache(patient.getPatientId());
        return updated;
    }

    @CacheEvict(value = PerformanceConfig.CACHE_PATIENTS, allEntries = true)
    public void clearPatientCache() {
    }

    @Cacheable(value = PerformanceConfig.CACHE_MEDICAL_RECORDS, key = "#patientId", unless = "#result.isEmpty()")
    public List<MedicalRecord> getMedicalRecords(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }

    @CacheEvict(value = PerformanceConfig.CACHE_MEDICAL_RECORDS, key = "#patientId")
    public void evictMedicalRecords(Long patientId) {
    }

    public void cachePatientData(Patient patient) {
        String key = PATIENT_KEY_PREFIX + patient.getPatientId();
        redisTemplate.opsForValue().set(key, patient, PATIENT_DIRECT_TTL);
    }

    public Patient getCachedPatient(Long patientId) {
        String key = PATIENT_KEY_PREFIX + patientId;
        return (Patient) redisTemplate.opsForValue().get(key);
    }

    public void evictPatientData(Long patientId) {
        evictPatientDirectCache(patientId);
    }

    private void evictPatientDirectCache(Long patientId) {
        String key = PATIENT_KEY_PREFIX + patientId;
        redisTemplate.delete(key);
    }
}

