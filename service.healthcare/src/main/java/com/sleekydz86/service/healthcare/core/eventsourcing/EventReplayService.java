package com.sleekydz86.service.healthcare.core.eventsourcing;

import com.sleekydz86.service.healthcare.core.eventsourcing.DomainEvent;
import com.sleekydz86.service.healthcare.core.domain.PatientAggregate;
import com.sleekydz86.service.healthcare.core.event.MedicalRecordAddedEvent;
import com.sleekydz86.service.healthcare.core.event.PatientCreatedEvent;
import com.sleekydz86.service.healthcare.core.event.PatientUpdatedEvent;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModel;
import com.sleekydz86.service.healthcare.core.readmodel.PatientReadModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventReplayService {

    private final EventStore eventStore;
    private final PatientReadModelRepository readModelRepository;
    private final AggregateRepository aggregateRepository;

    @Transactional
    public void replayEventsForAggregate(String aggregateId) {
        try {
            log.info("Aggregate 이벤트 재생 시작: {}", aggregateId);
            
            Optional<PatientAggregate> aggregateOpt = aggregateRepository.findById(aggregateId);
            
            if (aggregateOpt.isEmpty()) {
                log.warn("재생할 Aggregate를 찾을 수 없음: {}", aggregateId);
                return;
            }
            
            PatientAggregate aggregate = aggregateOpt.get();
            rebuildReadModel(aggregate);
            
            log.info("Aggregate 이벤트 재생 완료: {}", aggregateId);
        } catch (Exception e) {
            log.error("Aggregate 이벤트 재생 실패: {}", aggregateId, e);
            throw new RuntimeException("이벤트 재생 실패", e);
        }
    }

    @Transactional
    public void replayAllEvents() {
        try {
            log.info("전체 이벤트 재생 시작");
            
            List<String> aggregateIds = eventStore.getAllAggregateIds().join();
            
            for (String aggregateId : aggregateIds) {
                try {
                    replayEventsForAggregate(aggregateId);
                } catch (Exception e) {
                    log.error("Aggregate 이벤트 재생 실패: {}", aggregateId, e);
                }
            }
            
            log.info("전체 이벤트 재생 완료. 처리된 Aggregate 수: {}", aggregateIds.size());
        } catch (Exception e) {
            log.error("전체 이벤트 재생 실패", e);
            throw new RuntimeException("전체 이벤트 재생 실패", e);
        }
    }

    @Transactional
    public void replayEventsFromVersion(String aggregateId, int fromVersion) {
        try {
            log.info("버전 {}부터 Aggregate 이벤트 재생 시작: {}", fromVersion, aggregateId);
            
            List<DomainEvent> events = eventStore.getEventsAfterVersion(aggregateId, fromVersion).join();
            
            Optional<PatientAggregate> aggregateOpt = aggregateRepository.findById(aggregateId);
            
            if (aggregateOpt.isEmpty()) {
                log.warn("Aggregate not found for replay: {}", aggregateId);
                return;
            }
            
            PatientAggregate aggregate = aggregateOpt.get();
            
            for (DomainEvent event : events) {
                applyEventToAggregate(aggregate, event);
            }
            
            rebuildReadModel(aggregate);
            
            log.info("버전부터 Aggregate 이벤트 재생 완료: {}", aggregateId);
        } catch (Exception e) {
            log.error("버전부터 Aggregate 이벤트 재생 실패: {}", aggregateId, e);
            throw new RuntimeException("버전부터 이벤트 재생 실패", e);
        }
    }
    
    private void rebuildReadModel(PatientAggregate aggregate) {
        PatientReadModel readModel = new PatientReadModel(
            aggregate.getPatientId(),
            aggregate.getPatientName(),
            aggregate.getPhoneNumber(),
            aggregate.getEmail(),
            aggregate.getAddress(),
            aggregate.getMedicalHistory(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        readModelRepository.save(readModel);
        log.debug("Read model 재구성 완료 - Aggregate: {}", aggregate.getPatientId());
    }
    
    private void applyEventToAggregate(PatientAggregate aggregate, DomainEvent event) {
        if (event instanceof PatientUpdatedEvent) {
            PatientUpdatedEvent e = (PatientUpdatedEvent) event;
            aggregate.updatePatient(
                e.getPatientName(),
                e.getPhoneNumber(),
                e.getEmail(),
                e.getAddress()
            );
        } else if (event instanceof MedicalRecordAddedEvent) {
            MedicalRecordAddedEvent e = (MedicalRecordAddedEvent) event;
            aggregate.addMedicalRecord(e.getRecord());
        }
    }
}

