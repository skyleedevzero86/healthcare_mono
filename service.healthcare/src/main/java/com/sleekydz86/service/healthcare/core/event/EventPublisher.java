package com.sleekydz86.service.healthcare.core.event;

import com.sleekydz86.service.healthcare.dto.ApiResultCode;
import com.sleekydz86.service.healthcare.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("domainEventPublisher")
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange:healthcare.exchange}")
    private String exchange;

    public void publish(Object event) {
        try {
            String routingKey = "patient." + event.getClass().getSimpleName().toLowerCase();
            rabbitTemplate.convertAndSend(exchange, routingKey, event);
        } catch (Exception e) {
            throw new BusinessException("이벤트 발행 실패", e, ApiResultCode.UNKNOWN_ERR);
        }
    }
}
