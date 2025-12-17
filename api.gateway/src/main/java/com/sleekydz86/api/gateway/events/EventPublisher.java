package com.sleekydz86.api.gateway.events;

import com.sleekydz86.api.gateway.eventsourcing.DomainEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(DomainEvent event) {
        try {
            kafkaTemplate.send("healthcare-events", event.getEventType(), event);
        } catch (Exception e) {
            throw new RuntimeException("이벤트 발행 실패", e);
        }
    }
}
