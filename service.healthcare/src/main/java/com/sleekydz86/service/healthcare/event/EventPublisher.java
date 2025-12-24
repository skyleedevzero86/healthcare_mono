package com.sleekydz86.service.healthcare.event;

import com.sleekydz86.service.healthcare.dto.ApiResultCode;
import com.sleekydz86.service.healthcare.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component("healthDataEventPublisher")
@RequiredArgsConstructor
public class EventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${spring.rabbitmq.exchange:healthcare.exchange}")
    private String exchange;

    public void publishHealthDataEvent(HealthDataEvent event) {
        try {
            String routingKey = "health.data." + event.getDataType().toLowerCase();
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info("건강 데이터 이벤트 발행: eventId={}, type={}, userId={}", 
                event.getEventId(), event.getEventType(), event.getUserId());
        } catch (Exception e) {
            log.error("건강 데이터 이벤트 발행 실패", e);
            throw new BusinessException("이벤트 발행 실패", e, ApiResultCode.UNKNOWN_ERR);
        }
    }

    public void publishUserEvent(UserEvent event) {
        try {
            String routingKey = "user." + event.getEventType().toLowerCase();
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
            log.info("사용자 이벤트 발행: eventId={}, type={}, userId={}", 
                event.getEventId(), event.getEventType(), event.getUserId());
        } catch (Exception e) {
            log.error("사용자 이벤트 발행 실패", e);
            throw new BusinessException("이벤트 발행 실패", e, ApiResultCode.UNKNOWN_ERR);
        }
    }
}

