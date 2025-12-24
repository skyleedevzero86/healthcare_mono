package com.sleekydz86.service.commu.saga.steps;

import com.sleekydz86.service.commu.saga.Saga;
import com.sleekydz86.service.commu.saga.SagaStep;
import com.sleekydz86.service.commu.saga.SagaStepResult;
import com.sleekydz86.service.commu.saga.PatientRegistrationData;
import com.sleekydz86.service.commu.dto.NotificationRequest;
import com.sleekydz86.service.commu.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendWelcomeNotificationStep implements SagaStep {

    private final MessageService messageService;

    @Override
    public CompletableFuture<SagaStepResult> execute(Saga saga) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PatientRegistrationData data =
                    (PatientRegistrationData) saga.getData();

                if (data.getUserId() == null || data.getUserId().isEmpty()) {
                    log.warn("사용자 ID가 없어 환영 알림을 보낼 수 없습니다. Saga: {}", saga.getSagaId());
                    return new SagaStepResult(false, "User ID is required for welcome notification", null);
                }

                Long userId = Long.parseLong(data.getUserId());
                String welcomeMessage = String.format(
                    "안녕하세요 %s님! 건강관리 서비스에 가입해주셔서 감사합니다. " +
                    "건강한 하루 되세요!",
                    data.getPatientName() != null ? data.getPatientName() : "고객"
                );

                NotificationRequest notificationRequest = new NotificationRequest();
                notificationRequest.setUserId(userId);
                notificationRequest.setMessage(welcomeMessage);
                notificationRequest.setType("WELCOME");

                messageService.saveNotification(notificationRequest);

                data.setNotificationSent("true");
                saga.setData(data);

                log.info("환영 알림 전송 완료. 사용자 ID: {}, Saga: {}", userId, saga.getSagaId());
                return new SagaStepResult(true, "Welcome notification sent successfully", userId);

            } catch (NumberFormatException e) {
                log.error("사용자 ID 파싱 실패. Saga: {}", saga.getSagaId(), e);
                return new SagaStepResult(false, "Failed to parse user ID: " + e.getMessage(), null);
            } catch (Exception e) {
                log.error("환영 알림 전송 실패. Saga: {}", saga.getSagaId(), e);
                return new SagaStepResult(false, "Failed to send welcome notification: " + e.getMessage(), null);
            }
        });
    }

    @Override
    public CompletableFuture<SagaStepResult> compensate(Saga saga) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PatientRegistrationData data =
                    (PatientRegistrationData) saga.getData();

                if (data.getUserId() != null && "true".equals(data.getNotificationSent())) {
                    try {
                        Long userId = Long.parseLong(data.getUserId());
                        String cancellationMessage = String.format(
                            "%s님의 가입이 취소되었습니다.",
                            data.getPatientName() != null ? data.getPatientName() : "고객"
                        );

                        NotificationRequest cancellationRequest = new NotificationRequest();
                        cancellationRequest.setUserId(userId);
                        cancellationRequest.setMessage(cancellationMessage);
                        cancellationRequest.setType("CANCELLATION");

                        messageService.saveNotification(cancellationRequest);
                        log.info("가입 취소 알림 전송 완료. 사용자 ID: {}, Saga: {}", userId, saga.getSagaId());
                    } catch (NumberFormatException e) {
                        log.warn("사용자 ID 파싱 실패로 취소 알림을 보낼 수 없습니다. Saga: {}", saga.getSagaId());
                    }
                }

                return new SagaStepResult(true, "Welcome notification compensated", null);
            } catch (Exception e) {
                log.error("환영 알림 보상 트랜잭션 실패. Saga: {}", saga.getSagaId(), e);
                return new SagaStepResult(false, "Failed to compensate welcome notification: " + e.getMessage(), null);
            }
        });
    }

    @Override
    public String getStepName() {
        return "SendWelcomeNotification";
    }
}
