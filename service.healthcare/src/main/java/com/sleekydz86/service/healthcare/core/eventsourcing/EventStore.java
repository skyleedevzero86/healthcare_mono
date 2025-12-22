package com.sleekydz86.service.healthcare.core.eventsourcing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.healthcare.core.eventsourcing.DomainEvent;
import com.sleekydz86.service.healthcare.core.event.EventPublisher;
import com.sleekydz86.service.healthcare.dto.ApiResultCode;
import com.sleekydz86.service.healthcare.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStore {

    private final EventRepository eventRepository;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public CompletableFuture<Void> saveEvents(String aggregateId, List<DomainEvent> events, int expectedVersion) {
        return CompletableFuture.runAsync(() -> {
            try {
                int currentVersion = eventRepository.getLatestVersion(aggregateId);
                if (currentVersion != expectedVersion) {
                    throw new ConcurrencyException("예상 버전 " + expectedVersion +
                            "이지만 실제 버전은 " + currentVersion + "입니다");
                }

                int version = expectedVersion;
                for (DomainEvent event : events) {
                    version++;
                    EventEntity eventEntity = new EventEntity(
                            event.getEventId(),
                            event.getAggregateId(),
                            event.getEventType(),
                            version,
                            event.getTimestamp(),
                            serializeEvent(event));
                    eventRepository.save(eventEntity);
                }

                for (DomainEvent event : events) {
                    try {
                        eventPublisher.publish(event);
                    } catch (Exception e) {
                        log.error("이벤트 발행 실패: {}", event.getEventType(), e);
                    }
                }

            } catch (ConcurrencyException e) {
                throw e;
            } catch (Exception e) {
                throw new BusinessException("이벤트 저장 실패", e, ApiResultCode.UNKNOWN_ERR);
            }
        });
    }

    public CompletableFuture<List<DomainEvent>> getEvents(String aggregateId) {
        return CompletableFuture.supplyAsync(() -> {
            List<EventEntity> eventEntities = eventRepository.findByAggregateIdOrderByVersionAsc(aggregateId);
            return eventEntities.stream()
                    .map(this::deserializeEvent)
                    .collect(Collectors.toList());
        });
    }
    
    public CompletableFuture<List<DomainEvent>> getEventsAfterVersion(String aggregateId, int fromVersion) {
        return CompletableFuture.supplyAsync(() -> {
            List<EventEntity> eventEntities = eventRepository.findByAggregateIdAndVersionGreaterThanOrderByVersionAsc(aggregateId, fromVersion);
            return eventEntities.stream()
                    .map(this::deserializeEvent)
                    .collect(Collectors.toList());
        });
    }
    
    public CompletableFuture<List<String>> getAllAggregateIds() {
        return CompletableFuture.supplyAsync(() -> {
            return eventRepository.findAllDistinctAggregateIds();
        });
    }

    private String serializeEvent(DomainEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new BusinessException("이벤트 직렬화 실패", e, ApiResultCode.UNKNOWN_ERR);
        }
    }

    private DomainEvent deserializeEvent(EventEntity eventEntity) {
        try {
            String eventType = eventEntity.getEventType();
            Class<? extends DomainEvent> eventClass = getEventClass(eventType);
            return objectMapper.readValue(eventEntity.getEventData(), eventClass);
        } catch (Exception e) {
            throw new BusinessException("이벤트 역직렬화 실패", e, ApiResultCode.UNKNOWN_ERR);
        }
    }

    private Class<? extends DomainEvent> getEventClass(String eventType) {
        switch (eventType) {
            case "PatientCreated":
                return com.sleekydz86.service.healthcare.core.event.PatientCreatedEvent.class;
            case "AppointmentCreated":
                return com.sleekydz86.service.healthcare.core.event.AppointmentCreatedEvent.class;
            case "PatientUpdated":
                return com.sleekydz86.service.healthcare.core.event.PatientUpdatedEvent.class;
            case "MedicalRecordAdded":
                return com.sleekydz86.service.healthcare.core.event.MedicalRecordAddedEvent.class;
            default:
                throw new IllegalArgumentException("알 수 없는 이벤트 타입: " + eventType);
        }
    }
}
