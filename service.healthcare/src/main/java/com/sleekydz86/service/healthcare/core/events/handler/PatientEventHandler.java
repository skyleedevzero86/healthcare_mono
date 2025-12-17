package com.sleekydz86.service.healthcare.core.events.handler;

import com.sleekydz86.service.healthcare.core.event.PatientCreatedEvent;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModel;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PatientEventHandler {

    @Autowired
    private PatientReadModelRepository patientReadModelRepository;

    @KafkaListener(topics = "healthcare-events", groupId = "healthcare-service")
    public void handlePatientCreated(PatientCreatedEvent event) {
        try {
            PatientReadModel readModel = new PatientReadModel(
                    event.getAggregateId(),
                    event.getPatientName(),
                    event.getPhoneNumber(),
                    event.getEmail(),
                    event.getAddress(),
                    event.getMedicalHistory(),
                    LocalDateTime.now(),
                    LocalDateTime.now());

            patientReadModelRepository.save(readModel);

        } catch (Exception e) {
            System.err.println("PatientCreatedEvent 처리 실패: " + e.getMessage());
        }
    }
}
