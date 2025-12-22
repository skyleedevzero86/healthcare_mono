package com.sleekydz86.service.healthcare.core.events.handler;

import com.sleekydz86.service.healthcare.core.event.MedicalRecordAddedEvent;
import com.sleekydz86.service.healthcare.core.event.PatientCreatedEvent;
import com.sleekydz86.service.healthcare.core.event.PatientUpdatedEvent;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModel;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientEventHandler {

    private final PatientReadModelRepository patientReadModelRepository;

    @RabbitListener(queues = "${spring.rabbitmq.queue.patient-created:patient.created.queue}")
    @Transactional
    public void handlePatientCreated(PatientCreatedEvent event) {
        try {
            log.info("PatientCreatedEvent 처리 중: {}", event.getAggregateId());

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

            log.info("PatientCreatedEvent 처리 완료: {}", event.getAggregateId());
        } catch (Exception e) {
            log.error("PatientCreatedEvent 처리 실패: {}", event.getAggregateId(), e);
            throw new RuntimeException("PatientCreatedEvent 처리 실패", e);
        }
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.patient-updated:patient.updated.queue}")
    @Transactional
    public void handlePatientUpdated(PatientUpdatedEvent event) {
        try {
            log.info("PatientUpdatedEvent 처리 중: {}", event.getAggregateId());

            Optional<PatientReadModel> readModelOpt = patientReadModelRepository
                    .findByPatientId(event.getAggregateId());

            if (readModelOpt.isPresent()) {
                PatientReadModel readModel = readModelOpt.get();
                readModel.setPatientName(event.getPatientName());
                readModel.setPhoneNumber(event.getPhoneNumber());
                readModel.setEmail(event.getEmail());
                readModel.setAddress(event.getAddress());
                readModel.setUpdatedAt(LocalDateTime.now());

                patientReadModelRepository.save(readModel);

                log.info("PatientUpdatedEvent 처리 완료: {}", event.getAggregateId());
            } else {
                log.warn("업데이트할 PatientReadModel을 찾을 수 없음: {}", event.getAggregateId());
            }
        } catch (Exception e) {
            log.error("PatientUpdatedEvent 처리 실패: {}", event.getAggregateId(), e);
            throw new RuntimeException("PatientUpdatedEvent 처리 실패", e);
        }
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.medical-record-added:medical.record.added.queue}")
    @Transactional
    public void handleMedicalRecordAdded(MedicalRecordAddedEvent event) {
        try {
            log.info("MedicalRecordAddedEvent 처리 중: {}", event.getAggregateId());

            Optional<PatientReadModel> readModelOpt = patientReadModelRepository
                    .findByPatientId(event.getAggregateId());

            if (readModelOpt.isPresent()) {
                PatientReadModel readModel = readModelOpt.get();
                String currentHistory = readModel.getMedicalHistory();
                if (currentHistory == null) {
                    readModel.setMedicalHistory(event.getRecord());
                } else {
                    readModel.setMedicalHistory(currentHistory + "\n" + event.getRecord());
                }
                readModel.setUpdatedAt(LocalDateTime.now());

                patientReadModelRepository.save(readModel);

                log.info("MedicalRecordAddedEvent 처리 완료: {}", event.getAggregateId());
            } else {
                log.warn("의료 기록을 추가할 PatientReadModel을 찾을 수 없음: {}", event.getAggregateId());
            }
        } catch (Exception e) {
            log.error("MedicalRecordAddedEvent 처리 실패: {}", event.getAggregateId(), e);
            throw new RuntimeException("MedicalRecordAddedEvent 처리 실패", e);
        }
    }
}
