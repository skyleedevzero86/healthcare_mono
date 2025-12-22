package com.sleekydz86.service.healthcare.core.command.handler;

import com.sleekydz86.api.gateway.cqrs.command.CommandHandler;
import com.sleekydz86.service.healthcare.core.command.CreatePatientCommand;
import com.sleekydz86.service.healthcare.core.domain.PatientAggregate;
import com.sleekydz86.service.healthcare.core.eventsourcing.AggregateRepository;
import com.sleekydz86.service.healthcare.core.eventsourcing.EventStore;
import com.sleekydz86.service.healthcare.dto.ApiResultCode;
import com.sleekydz86.service.healthcare.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreatePatientCommandHandler implements CommandHandler<CreatePatientCommand, String> {

    private final AggregateRepository aggregateRepository;
    private final EventStore eventStore;

    @Override
    @Transactional
    public CompletableFuture<String> handle(CreatePatientCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String aggregateId = command.getAggregateId();
                if (aggregateId == null || aggregateId.isEmpty()) {
                    aggregateId = UUID.randomUUID().toString();
                }
                
                java.util.Optional<PatientAggregate> existingAggregate = 
                    aggregateRepository.findById(aggregateId);
                
                if (existingAggregate.isPresent()) {
                    throw new BusinessException("환자가 이미 존재합니다: " + aggregateId, ApiResultCode.DUPLICATE_DATA);
                }
                
                PatientAggregate aggregate = new PatientAggregate(
                    aggregateId,
                    command.getPatientName(),
                    command.getPhoneNumber(),
                    command.getEmail(),
                    command.getAddress(),
                    command.getMedicalHistory() != null ? command.getMedicalHistory() : ""
                );
                
                aggregateRepository.save(aggregate);
                
                log.info("Event Sourcing을 통한 환자 생성 완료: {}", aggregateId);
                return aggregateId;
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.error("환자 생성 실패", e);
                throw new BusinessException("환자 생성 실패", e, ApiResultCode.UNKNOWN_ERR);
            }
        });
    }

    @Override
    public Class<CreatePatientCommand> getCommandType() {
        return CreatePatientCommand.class;
    }
}
