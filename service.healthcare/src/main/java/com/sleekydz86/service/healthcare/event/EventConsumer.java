package com.sleekydz86.service.healthcare.event;

import com.sleekydz86.service.healthcare.eventsourcing.EventHandler;
import com.sleekydz86.service.healthcare.eventsourcing.EventStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

    @Qualifier("healthDataEventStore")
    private final EventStore eventStore;
    private final EventHandler eventHandler;

    @RabbitListener(queues = "${spring.rabbitmq.queue.health-data:health.data.queue}")
    public void handleHealthDataEvent(HealthDataEvent event) {
        log.info("건강 데이터 이벤트 수신: eventId={}, type={}, userId={}",
                event.getEventId(), event.getEventType(), event.getUserId());

        try {
            processHealthDataEvent(event);
        } catch (Exception e) {
            log.error("건강 데이터 이벤트 처리 중 오류 발생: eventId={}", event.getEventId(), e);
        }
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.user:user.queue}")
    public void handleUserEvent(UserEvent event) {
        log.info("사용자 이벤트 수신: eventId={}, type={}, userId={}",
                event.getEventId(), event.getEventType(), event.getUserId());

        try {
            processUserEvent(event);
        } catch (Exception e) {
            log.error("사용자 이벤트 처리 중 오류 발생: eventId={}", event.getEventId(), e);
        }
    }

    private void processHealthDataEvent(HealthDataEvent event) {
        eventStore.saveEvent(event);
        eventHandler.handle(event);
        log.debug("건강 데이터 이벤트 처리 완료: eventId={}, type={}",
                event.getEventId(), event.getEventType());
    }

    private void processUserEvent(UserEvent event) {
        log.info("사용자 이벤트 처리: eventId={}, type={}, userId={}",
                event.getEventId(), event.getEventType(), event.getUserId());

        switch (event.getEventType()) {
            case "USER_CREATED":
                log.info("새 사용자 생성: userId={}", event.getUserId());
                break;
            case "USER_UPDATED":
                log.info("사용자 정보 업데이트: userId={}", event.getUserId());
                break;
            case "USER_DELETED":
                log.info("사용자 삭제: userId={}", event.getUserId());
                break;
            case "USER_LOGIN":
                log.info("사용자 로그인: userId={}", event.getUserId());
                break;
            case "USER_LOGOUT":
                log.info("사용자 로그아웃: userId={}", event.getUserId());
                break;
            default:
                log.warn("알 수 없는 사용자 이벤트 타입: {}", event.getEventType());
        }
    }
}
