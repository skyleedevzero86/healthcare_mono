package com.sleekydz86.service.healthcare.service.sharding;

import com.sleekydz86.service.healthcare.entity.MedicalRecord;
import com.sleekydz86.service.healthcare.global.mapper.MedicalRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicalRecordShardingService {

    private final MedicalRecordMapper medicalRecordMapper;

    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecord.getVisitDate() == null) {
            medicalRecord.setVisitDate(LocalDateTime.now());
        }
        medicalRecord.setCreatedAt(LocalDateTime.now());
        medicalRecord.setUpdatedAt(LocalDateTime.now());
        medicalRecordMapper.insert(medicalRecord);
        return medicalRecord;
    }

    public List<MedicalRecord> findMedicalRecordsByPatientId(Long patientId) {
        return medicalRecordMapper.findByPatientId(patientId);
    }

    public List<MedicalRecord> findMedicalRecordsByPatientIdAndDateRange(Long patientId, LocalDateTime startDate, LocalDateTime endDate) {
        return medicalRecordMapper.findByPatientIdAndVisitDateBetween(patientId, startDate, endDate);
    }

    public List<MedicalRecord> findMedicalRecordsByDoctorName(String doctorName) {
        return medicalRecordMapper.findByDoctorName(doctorName);
    }

    public MedicalRecord updateMedicalRecord(MedicalRecord medicalRecord) {
        medicalRecord.setUpdatedAt(LocalDateTime.now());
        medicalRecordMapper.update(medicalRecord);
        return medicalRecord;
    }

    public void deleteMedicalRecord(Long recordId) {
        medicalRecordMapper.deleteById(recordId);
    }
}

