package com.sleekydz86.service.commu.saga.steps;

// TODO: 서비스 간 직접 의존성 제거 필요 - HTTP 통신으로 변경
// import com.sleekydz86.api.gateway.saga.Saga;
// import com.sleekydz86.api.gateway.saga.SagaStep;
// import com.sleekydz86.api.gateway.saga.SagaStepResult;
import com.sleekydz86.service.commu.dto.NotificationRequest;
import com.sleekydz86.service.commu.service.MessageService;
// import com.sleekydz86.service.healthcare.core.saga.PatientRegistrationSaga;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class SendWelcomeNotificationStep {
    // TODO: 서비스 간 직접 의존성 제거 필요 - HTTP 통신으로 변경
    // implements SagaStep {

    private final MessageService messageService;

    // @Override
    // public CompletableFuture<SagaStepResult> execute(Saga saga) {
    // return CompletableFuture.supplyAsync(() -> {
    // try {
    // PatientRegistrationSaga.PatientRegistrationData data =
    // (PatientRegistrationSaga.PatientRegistrationData) saga.getData();
    //
    // NotificationRequest request = new NotificationRequest();
    // request.setUserId(data.getUserId() != null ? Long.parseLong(data.getUserId())
    // : null);
    // request.setMessage("Welcome " + data.getPatientName() + "! Thank you for
    // registering.");
    // request.setType("WELCOME");
    //
    // messageService.saveNotification(request);
    //
    // data.setNotificationSent("true");
    // saga.setData(data);
    //
    // return new SagaStepResult(true, "Welcome notification sent successfully",
    // null);
    // } catch (Exception e) {
    // return new SagaStepResult(false, "Failed to send welcome notification: " +
    // e.getMessage(), null);
    // }
    // });
    // }
    //
    // @Override
    // public CompletableFuture<SagaStepResult> compensate(Saga saga) {
    // return CompletableFuture.supplyAsync(() -> {
    // try {
    // return new SagaStepResult(true, "Notification compensation logged", null);
    // } catch (Exception e) {
    // return new SagaStepResult(false, "Failed to log notification compensation: "
    // + e.getMessage(), null);
    // }
    // });
    // }
    //
    // @Override
    // public String getStepName() {
    // return "SendWelcomeNotification";
    // }
}
