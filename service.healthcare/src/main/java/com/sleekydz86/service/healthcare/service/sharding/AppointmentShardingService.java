package com.sleekydz86.service.healthcare.service.sharding;

import com.sleekydz86.service.healthcare.entity.Appointment;
import com.sleekydz86.service.healthcare.global.mapper.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AppointmentShardingService {

    private final AppointmentMapper appointmentMapper;
    private final ShardingKeyGenerator shardingKeyGenerator;

    public Appointment createAppointment(Appointment appointment) {
        if (appointment.getDateHash() == null && appointment.getAppointmentDate() != null) {
            int dateHash = Math.abs(appointment.getAppointmentDate().hashCode());
            appointment.setDateHash(dateHash);
        }
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentMapper.insert(appointment);
        return appointment;
    }

    public List<Appointment> findAppointmentsByPatientId(Long patientId) {
        return appointmentMapper.findByPatientId(patientId);
    }

    public List<Appointment> findAppointmentsByDoctorId(Long doctorId) {
        return appointmentMapper.findByDoctorId(doctorId);
    }

    public List<Appointment> findAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentMapper.findByAppointmentDateBetween(startDate, endDate);
    }

    public List<Appointment> findUpcomingAppointmentsByPatientId(Long patientId) {
        return appointmentMapper.findUpcomingByPatientId(patientId, LocalDateTime.now());
    }

    public List<Appointment> findAppointmentsByStatus(String status) {
        return appointmentMapper.findByStatus(status);
    }

    public Appointment updateAppointment(Appointment appointment) {
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentMapper.update(appointment);
        return appointment;
    }

    public void deleteAppointment(Long appointmentId) {
        appointmentMapper.deleteById(appointmentId);
    }
}

