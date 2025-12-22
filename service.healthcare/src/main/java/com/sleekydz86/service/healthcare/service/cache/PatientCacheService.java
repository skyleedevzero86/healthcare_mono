package com.sleekydz86.service.healthcare.service.cache;

import com.sleekydz86.service.healthcare.config.PerformanceConfig;
import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.global.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PatientCacheService {

    private static final String PATIENT_CACHE_PREFIX = "patient:";
    private static final Duration CACHE_TTL = PerformanceConfig.PATIENTS_TTL;

    private final PatientMapper patientMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public Patient getPatient(Long patientId) {
        String cacheKey = PATIENT_CACHE_PREFIX + patientId;

        Patient cachedPatient = (Patient) redisTemplate.opsForValue().get(cacheKey);
        if (cachedPatient != null) {
            return cachedPatient;
        }

        Patient patient = patientMapper.findById(patientId);
        if (patient != null) {
            redisTemplate.opsForValue().set(cacheKey, patient, CACHE_TTL);
        }

        return patient;
    }

    public Patient updatePatient(Patient patient) {
        patientMapper.update(patient);
        Patient updatedPatient = patientMapper.findById(patient.getPatientId());

        String cacheKey = PATIENT_CACHE_PREFIX + patient.getPatientId();
        redisTemplate.opsForValue().set(cacheKey, updatedPatient, CACHE_TTL);

        return updatedPatient;
    }

    public void deletePatient(Long patientId) {
        patientMapper.deleteById(patientId);
        evictPatientCache(patientId);
    }

    public void evictPatientCache(Long patientId) {
        String cacheKey = PATIENT_CACHE_PREFIX + patientId;
        redisTemplate.delete(cacheKey);
    }
}

