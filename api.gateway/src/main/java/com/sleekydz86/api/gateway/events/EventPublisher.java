package com.sleekydz86.api.gateway.events;

import com.sleekydz86.api.gateway.dto.ApiResultCode;
import com.sleekydz86.api.gateway.eventsourcing.DomainEvent;
import com.sleekydz86.api.gateway.exception.BusinessException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(DomainEvent event) {
        try {
            kafkaTemplate.send("healthcare-events", event.getEventType(), event);
        } catch (Exception e) {
            throw new BusinessException("이벤트 발행 실패", e, ApiResultCode.INTERNAL_ERROR);
        }
    }
}
