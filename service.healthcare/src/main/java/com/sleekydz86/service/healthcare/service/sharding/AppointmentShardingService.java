package com.sleekydz86.service.healthcare.service.sharding;

import com.sleekydz86.service.healthcare.entity.Appointment;
import com.sleekydz86.service.healthcare.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AppointmentShardingService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ShardingKeyGenerator shardingKeyGenerator;

    public Appointment createAppointment(Appointment appointment) {
        if (appointment.getDateHash() == null && appointment.getAppointmentDate() != null) {
            int dateHash = Math.abs(appointment.getAppointmentDate().hashCode());
            appointment.setDateHash(dateHash);
        }
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> findAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> findAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    public List<Appointment> findAppointmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.findByAppointmentDateBetween(startDate, endDate);
    }

    public List<Appointment> findUpcomingAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findUpcomingByPatientId(patientId, LocalDateTime.now());
    }

    public List<Appointment> findAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    public Appointment updateAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }
}

