package com.sleekydz86.api.gateway.events;

import com.sleekydz86.api.gateway.dto.ApiResultCode;
import com.sleekydz86.api.gateway.eventsourcing.DomainEvent;
import com.sleekydz86.api.gateway.exception.BusinessException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;
    
    @Value("${spring.rabbitmq.exchange:healthcare.exchange}")
    private String exchange;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(DomainEvent event) {
        try {
            rabbitTemplate.convertAndSend(exchange, event.getEventType(), event);
        } catch (Exception e) {
            throw new BusinessException("이벤트 발행 실패", e, ApiResultCode.INTERNAL_ERROR);
        }
    }
}
