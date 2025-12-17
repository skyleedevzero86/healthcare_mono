package com.sleekydz86.service.healthcare.repository;

import com.sleekydz86.service.healthcare.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByAppointmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Appointment> findByDateHash(Integer dateHash);

    @Query("SELECT a FROM Appointment a WHERE a.patientId = :patientId AND a.appointmentDate >= :date")
    List<Appointment> findUpcomingByPatientId(@Param("patientId") Long patientId, @Param("date") LocalDateTime date);

    @Query("SELECT a FROM Appointment a WHERE a.status = :status")
    List<Appointment> findByStatus(@Param("status") String status);
}

