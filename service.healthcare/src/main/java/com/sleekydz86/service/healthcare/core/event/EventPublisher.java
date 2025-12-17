package com.sleekydz86.service.healthcare.core.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Value("${spring.rabbitmq.exchange:healthcare.exchange}")
    private String exchange;

    public void publish(Object event) {
        try {
            String routingKey = "patient." + event.getClass().getSimpleName().toLowerCase();
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
        } catch (Exception e) {
            throw new RuntimeException("이벤트 발행 실패", e);
        }
    }
}

