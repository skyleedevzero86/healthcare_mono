package com.sleekydz86.service.usermanagement.saga.steps;

import com.sleekydz86.service.usermanagement.saga.Saga;
import com.sleekydz86.service.usermanagement.saga.SagaStep;
import com.sleekydz86.service.usermanagement.saga.SagaStepResult;
import com.sleekydz86.service.usermanagement.saga.PatientRegistrationData;
import com.sleekydz86.service.usermanagement.entity.User;
import com.sleekydz86.service.usermanagement.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class CreateUserAccountStep implements SagaStep {

    private final UserMapper userMapper;

    @Override
    public CompletableFuture<SagaStepResult> execute(Saga saga) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PatientRegistrationData data = (PatientRegistrationData) saga.getData();

                User user = new User();
                user.setName(data.getPatientName());
                user.setEmail(data.getEmail());
                user.setUsername(data.getEmail());
                user.setPassword("default");
                user.setRole("PATIENT");

                userMapper.insert(user);
                String userId = String.valueOf(user.getId());

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
                PatientRegistrationData data = (PatientRegistrationData) saga.getData();

                if (data.getUserId() != null) {
                    try {
                        Long userId = Long.parseLong(data.getUserId());
                        userMapper.deleteById(userId);
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
