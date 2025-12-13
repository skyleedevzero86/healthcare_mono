package com.sleekydz86.service.healthcare.eventsourcing;

import com.sleekydz86.service.healthcare.event.HealthDataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandlerImpl implements EventHandler {

    @Override
    public void handle(HealthDataEvent event) {
        try {
            log.debug("이벤트 처리 시작: eventId={}, type={}, userId={}, dataType={}", 
                event.getEventId(), event.getEventType(), event.getUserId(), event.getDataType());
            
            switch (event.getEventType()) {
                case "INSERT":
                    handleInsertEvent(event);
                    break;
                case "UPDATE":
                    handleUpdateEvent(event);
                    break;
                case "DELETE":
                    handleDeleteEvent(event);
                    break;
                default:
                    log.warn("알 수 없는 이벤트 타입: {}", event.getEventType());
            }
            
            log.debug("이벤트 처리 완료: eventId={}", event.getEventId());
        } catch (Exception e) {
            log.error("이벤트 처리 중 오류 발생: eventId={}", event.getEventId(), e);
            throw new RuntimeException("이벤트 처리 실패", e);
        }
    }

    private void handleInsertEvent(HealthDataEvent event) {
        log.info("건강 데이터 삽입 이벤트 처리: userId={}, dataType={}", 
            event.getUserId(), event.getDataType());
    }

    private void handleUpdateEvent(HealthDataEvent event) {
        log.info("건강 데이터 업데이트 이벤트 처리: userId={}, dataType={}", 
            event.getUserId(), event.getDataType());
    }

    private void handleDeleteEvent(HealthDataEvent event) {
        log.info("건강 데이터 삭제 이벤트 처리: userId={}, dataType={}", 
            event.getUserId(), event.getDataType());
    }
}

