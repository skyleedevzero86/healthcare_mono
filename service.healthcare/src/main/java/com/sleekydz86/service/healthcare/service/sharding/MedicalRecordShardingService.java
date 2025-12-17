package com.sleekydz86.service.healthcare.service.sharding;

import com.sleekydz86.service.healthcare.entity.MedicalRecord;
import com.sleekydz86.service.healthcare.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MedicalRecordShardingService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        return medicalRecordRepository.save(medicalRecord);
    }

    public List<MedicalRecord> findMedicalRecordsByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }

    public List<MedicalRecord> findMedicalRecordsByPatientIdAndDateRange(Long patientId, LocalDateTime startDate, LocalDateTime endDate) {
        return medicalRecordRepository.findByPatientIdAndVisitDateBetween(patientId, startDate, endDate);
    }

    public List<MedicalRecord> findMedicalRecordsByDoctorName(String doctorName) {
        return medicalRecordRepository.findByDoctorName(doctorName);
    }

    public MedicalRecord updateMedicalRecord(MedicalRecord medicalRecord) {
        return medicalRecordRepository.save(medicalRecord);
    }

    public void deleteMedicalRecord(Long recordId) {
        medicalRecordRepository.deleteById(recordId);
    }
}

