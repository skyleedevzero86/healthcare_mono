package com.sleekydz86.service.healthcare.eventsourcing;

import com.sleekydz86.service.healthcare.event.HealthDataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventStore {
    
    private final Map<String, List<HealthDataEvent>> eventStore = new ConcurrentHashMap<>();

    public void saveEvent(HealthDataEvent event) {
        eventStore.computeIfAbsent(event.getUserId(), k -> new ArrayList<>())
            .add(event);
        log.debug("이벤트 저장: userId={}, eventId={}, type={}", 
            event.getUserId(), event.getEventId(), event.getEventType());
    }

    public List<HealthDataEvent> getEventsByUserId(String userId) {
        return eventStore.getOrDefault(userId, new ArrayList<>());
    }

    public List<HealthDataEvent> getEventsByType(String userId, String eventType) {
        return getEventsByUserId(userId).stream()
            .filter(event -> event.getEventType().equals(eventType))
            .collect(Collectors.toList());
    }

    public List<HealthDataEvent> getEventsByDataType(String userId, String dataType) {
        return getEventsByUserId(userId).stream()
            .filter(event -> event.getDataType().equals(dataType))
            .collect(Collectors.toList());
    }

    public void replayEvents(String userId, EventHandler handler) {
        List<HealthDataEvent> events = getEventsByUserId(userId);
        for (HealthDataEvent event : events) {
            handler.handle(event);
        }
        log.info("이벤트 재생 완료: userId={}, eventCount={}", userId, events.size());
    }
}

