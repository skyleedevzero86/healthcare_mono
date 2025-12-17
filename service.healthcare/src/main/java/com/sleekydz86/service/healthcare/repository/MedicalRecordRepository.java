package com.sleekydz86.service.healthcare.repository;

import com.sleekydz86.service.healthcare.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPatientId(Long patientId);

    List<MedicalRecord> findByPatientIdAndVisitDateBetween(Long patientId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patientId = :patientId ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByPatientIdOrderByVisitDateDesc(@Param("patientId") Long patientId);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.doctorName = :doctorName")
    List<MedicalRecord> findByDoctorName(@Param("doctorName") String doctorName);
}

