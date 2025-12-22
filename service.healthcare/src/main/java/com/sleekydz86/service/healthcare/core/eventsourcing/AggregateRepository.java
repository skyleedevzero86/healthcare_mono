package com.sleekydz86.service.healthcare.core.eventsourcing;

import com.sleekydz86.service.healthcare.core.eventsourcing.DomainEvent;
import com.sleekydz86.service.healthcare.core.domain.PatientAggregate;
import com.sleekydz86.service.healthcare.core.event.MedicalRecordAddedEvent;
import com.sleekydz86.service.healthcare.core.event.PatientCreatedEvent;
import com.sleekydz86.service.healthcare.core.event.PatientUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AggregateRepository {

    private final EventStore eventStore;

    @Transactional
    public void save(PatientAggregate aggregate) {
        try {
            List<DomainEvent> uncommittedEvents = aggregate.getUncommittedEvents();
            if (!uncommittedEvents.isEmpty()) {
                int expectedVersion = aggregate.getVersion() - uncommittedEvents.size();
                eventStore.saveEvents(aggregate.getPatientId(), uncommittedEvents, expectedVersion).join();
                aggregate.markEventsAsCommitted();
                log.debug("Aggregate 저장 완료: {} (이벤트 {}개)", aggregate.getPatientId(), uncommittedEvents.size());
            }
        } catch (Exception e) {
            log.error("Aggregate 저장 실패: {}", aggregate.getPatientId(), e);
            throw new RuntimeException("Aggregate 저장 실패", e);
        }
    }

    public Optional<PatientAggregate> findById(String aggregateId) {
        try {
            List<DomainEvent> events = eventStore.getEvents(aggregateId).join();

            if (events.isEmpty()) {
                return Optional.empty();
            }

            PatientAggregate aggregate = recreateAggregateFromEvents(events);
            return Optional.of(aggregate);
        } catch (Exception e) {
            log.error("Aggregate 조회 실패: {}", aggregateId, e);
            return Optional.empty();
        }
    }

    private PatientAggregate recreateAggregateFromEvents(List<DomainEvent> events) {
        if (events == null || events.isEmpty()) {
            throw new IllegalStateException("빈 이벤트로부터 Aggregate를 재생성할 수 없습니다");
        }

        DomainEvent firstEvent = events.get(0);
        if (!(firstEvent instanceof PatientCreatedEvent)) {
            throw new IllegalStateException("첫 번째 이벤트는 PatientCreatedEvent여야 합니다");
        }

        PatientCreatedEvent createdEvent = (PatientCreatedEvent) firstEvent;
        PatientAggregate aggregate = new PatientAggregate(
                createdEvent.getAggregateId(),
                createdEvent.getPatientName(),
                createdEvent.getPhoneNumber(),
                createdEvent.getEmail(),
                createdEvent.getAddress(),
                createdEvent.getMedicalHistory());

        for (int i = 1; i < events.size(); i++) {
            DomainEvent event = events.get(i);
            if (event instanceof PatientUpdatedEvent) {
                PatientUpdatedEvent e = (PatientUpdatedEvent) event;
                aggregate.updatePatient(
                        e.getPatientName(),
                        e.getPhoneNumber(),
                        e.getEmail(),
                        e.getAddress());
            } else if (event instanceof MedicalRecordAddedEvent) {
                MedicalRecordAddedEvent e = (MedicalRecordAddedEvent) event;
                aggregate.addMedicalRecord(e.getRecord());
            }
        }

        aggregate.markEventsAsCommitted();
        return aggregate;
    }

    public Optional<PatientAggregate> findByIdWithSnapshot(String aggregateId, int snapshotVersion) {
        try {
            List<DomainEvent> events = eventStore.getEventsAfterVersion(aggregateId, snapshotVersion).join();

            if (events.isEmpty()) {
                return Optional.empty();
            }

            PatientAggregate aggregate = recreateAggregateFromEvents(events);
            return Optional.of(aggregate);
        } catch (Exception e) {
            log.error("스냅샷으로 Aggregate 조회 실패: {}", aggregateId, e);
            return Optional.empty();
        }
    }
}
