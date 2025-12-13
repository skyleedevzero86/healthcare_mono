package com.sleekydz86.service.healthcare.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

    @RabbitListener(queues = "${spring.rabbitmq.queue.health-data:health.data.queue}")
    public void handleHealthDataEvent(HealthDataEvent event) {
        log.info("건강 데이터 이벤트 수신: eventId={}, type={}, userId={}", 
            event.getEventId(), event.getEventType(), event.getUserId());
        
        processHealthDataEvent(event);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.user:user.queue}")
    public void handleUserEvent(UserEvent event) {
        log.info("사용자 이벤트 수신: eventId={}, type={}, userId={}", 
            event.getEventId(), event.getEventType(), event.getUserId());
        
        processUserEvent(event);
    }
    
    private void processHealthDataEvent(HealthDataEvent event) {
    }
    
    private void processUserEvent(UserEvent event) {
    }
}

