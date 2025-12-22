package com.sleekydz86.service.healthcare.global.mapper;

import com.sleekydz86.service.healthcare.entity.MedicalRecord;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MedicalRecordMapper {
    List<MedicalRecord> findByPatientId(Long patientId);
    List<MedicalRecord> findByPatientIdAndVisitDateBetween(Long patientId, LocalDateTime startDate, LocalDateTime endDate);
    List<MedicalRecord> findByDoctorName(String doctorName);
    MedicalRecord findById(Long recordId);
    int insert(MedicalRecord record);
    int update(MedicalRecord record);
    int deleteById(Long recordId);
}

