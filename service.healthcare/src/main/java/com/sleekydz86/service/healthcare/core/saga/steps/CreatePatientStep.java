package com.sleekydz86.service.healthcare.core.saga.steps;

import com.sleekydz86.api.gateway.saga.Saga;
import com.sleekydz86.api.gateway.saga.SagaStep;
import com.sleekydz86.api.gateway.saga.SagaStepResult;
import com.sleekydz86.service.healthcare.core.saga.PatientRegistrationSaga;
import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class CreatePatientStep implements SagaStep {

    private final PatientService patientService;

    @Override
    public CompletableFuture<SagaStepResult> execute(Saga saga) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PatientRegistrationSaga.PatientRegistrationData data =
                    (PatientRegistrationSaga.PatientRegistrationData) saga.getData();

                Patient patient = new Patient();
                patient.setName(data.getPatientName());
                patient.setPhone(data.getPhoneNumber());
                patient.setEmail(data.getEmail());
                patient.setAddress(data.getAddress());
                patient.setRegionId("default");

                Patient createdPatient = patientService.createPatient(patient);
                String patientId = String.valueOf(createdPatient.getPatientId());

                data.setPatientId(patientId);
                saga.setData(data);

                return new SagaStepResult(true, "Patient created successfully", patientId);
            } catch (Exception e) {
                return new SagaStepResult(false, "Failed to create patient: " + e.getMessage(), null);
            }
        });
    }

    @Override
    public CompletableFuture<SagaStepResult> compensate(Saga saga) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PatientRegistrationSaga.PatientRegistrationData data =
                    (PatientRegistrationSaga.PatientRegistrationData) saga.getData();

                if (data.getPatientId() != null) {
                    try {
                        Long patientId = Long.parseLong(data.getPatientId());
                        patientService.deletePatient(patientId);
                    } catch (NumberFormatException e) {
                    }
                }

                return new SagaStepResult(true, "Patient creation compensated", null);
            } catch (Exception e) {
                return new SagaStepResult(false, "Failed to compensate patient creation: " + e.getMessage(), null);
            }
        });
    }

    @Override
    public String getStepName() {
        return "CreatePatient";
    }
}

