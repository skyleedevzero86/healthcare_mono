package com.sleekydz86.service.usermanagement.saga.steps;

import com.sleekydz86.api.gateway.saga.Saga;
import com.sleekydz86.api.gateway.saga.SagaStep;
import com.sleekydz86.api.gateway.saga.SagaStepResult;
import com.sleekydz86.service.healthcare.core.saga.PatientRegistrationSaga;
import com.sleekydz86.service.usermanagement.entity.User;
import com.sleekydz86.service.usermanagement.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class CreateUserAccountStep implements SagaStep {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Override
    public CompletableFuture<SagaStepResult> execute(Saga saga) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PatientRegistrationSaga.PatientRegistrationData data =
                    (PatientRegistrationSaga.PatientRegistrationData) saga.getData();

                User user = new User();
                user.setName(data.getPatientName());
                user.setEmail(data.getEmail());
                user.setUsername(data.getEmail());
                user.setPassword("default");
                user.setRole("PATIENT");

                User createdUser = userJpaRepository.save(user);
                String userId = String.valueOf(createdUser.getId());

                data.setUserId(userId);
                data.setUserAccountCreated("true");
                saga.setData(data);

                return new SagaStepResult(true, "User account created successfully", userId);
            } catch (Exception e) {
                return new SagaStepResult(false, "Failed to create user account: " + e.getMessage(), null);
            }
        });
    }

    @Override
    public CompletableFuture<SagaStepResult> compensate(Saga saga) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PatientRegistrationSaga.PatientRegistrationData data =
                    (PatientRegistrationSaga.PatientRegistrationData) saga.getData();

                if (data.getUserId() != null) {
                    try {
                        Long userId = Long.parseLong(data.getUserId());
                        userJpaRepository.deleteById(userId);
                    } catch (NumberFormatException e) {
                    }
                }

                return new SagaStepResult(true, "User account creation compensated", null);
            } catch (Exception e) {
                return new SagaStepResult(false, "Failed to compensate user account creation: " + e.getMessage(), null);
            }
        });
    }

    @Override
    public String getStepName() {
        return "CreateUserAccount";
    }
}

