package com.sleekydz86.service.healthcare.global.mapper;

import com.sleekydz86.service.healthcare.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AppointmentMapper {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByAppointmentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Appointment> findByStatus(String status);
    List<Appointment> findUpcomingByPatientId(@Param("patientId") Long patientId, @Param("now") LocalDateTime now);
    Appointment findById(Long appointmentId);
    int insert(Appointment appointment);
    int update(Appointment appointment);
    int deleteById(Long appointmentId);
}

