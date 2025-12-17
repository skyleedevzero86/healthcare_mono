package com.sleekydz86.service.healthcare.core.command.handler;

import com.sleekydz86.api.gateway.cqrs.command.CommandHandler;
import com.sleekydz86.service.healthcare.core.command.CreatePatientCommand;
import com.sleekydz86.service.healthcare.core.event.EventPublisher;
import com.sleekydz86.service.healthcare.core.event.PatientCreatedEvent;
import com.sleekydz86.service.healthcare.dto.ApiResultCode;
import com.sleekydz86.service.healthcare.entity.Patient;
import com.sleekydz86.service.healthcare.exception.BusinessException;
import com.sleekydz86.service.healthcare.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class CreatePatientCommandHandler implements CommandHandler<CreatePatientCommand, String> {

    private final PatientRepository patientRepository;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public CompletableFuture<String> handle(CreatePatientCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Patient patient = new Patient();
                try {
                    patient.setPatientId(Long.parseLong(command.getPatientId()));
                } catch (NumberFormatException e) {
                }
                patient.setName(command.getPatientName());
                patient.setPhone(command.getPhoneNumber());
                patient.setEmail(command.getEmail());
                patient.setAddress(command.getAddress());

                patientRepository.save(patient);

                PatientCreatedEvent event = new PatientCreatedEvent(
                    command.getAggregateId(),
                    command.getPatientName(),
                    command.getPhoneNumber(),
                    command.getEmail(),
                    command.getAddress(),
                    command.getMedicalHistory()
                );
                eventPublisher.publish(event);

                return command.getPatientId();
            } catch (Exception e) {
                throw new BusinessException("환자 생성 실패", e, ApiResultCode.UNKOWN_ERR);
            }
        });
    }

    @Override
    public Class<CreatePatientCommand> getCommandType() {
        return CreatePatientCommand.class;
    }
}

