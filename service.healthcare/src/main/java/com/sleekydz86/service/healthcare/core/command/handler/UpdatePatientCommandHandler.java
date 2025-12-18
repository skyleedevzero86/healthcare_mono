package com.sleekydz86.service.healthcare.core.command.handler;

import com.sleekydz86.api.gateway.cqrs.command.CommandHandler;
import com.sleekydz86.service.healthcare.core.command.UpdatePatientCommand;
import com.sleekydz86.service.healthcare.core.domain.PatientAggregate;
import com.sleekydz86.service.healthcare.core.eventsourcing.AggregateRepository;
import com.sleekydz86.service.healthcare.dto.ApiResultCode;
import com.sleekydz86.service.healthcare.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdatePatientCommandHandler implements CommandHandler<UpdatePatientCommand, String> {

    private final AggregateRepository aggregateRepository;

    @Override
    @Transactional
    public CompletableFuture<String> handle(UpdatePatientCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<PatientAggregate> aggregateOpt = 
                    aggregateRepository.findById(command.getAggregateId());
                
                if (aggregateOpt.isEmpty()) {
                    throw new BusinessException("환자를 찾을 수 없습니다: " + command.getAggregateId(), 
                        ApiResultCode.RESULT_IS_EMPTY);
                }
                
                PatientAggregate aggregate = aggregateOpt.get();
                aggregate.updatePatient(
                    command.getPatientName(),
                    command.getPhoneNumber(),
                    command.getEmail(),
                    command.getAddress()
                );
                
                aggregateRepository.save(aggregate);
                
                log.info("Event Sourcing을 통한 환자 업데이트 완료: {}", command.getAggregateId());
                return command.getAggregateId();
            } catch (BusinessException e) {
                throw e;
            } catch (Exception e) {
                log.error("환자 업데이트 실패", e);
                throw new BusinessException("환자 업데이트 실패", e, ApiResultCode.UNKOWN_ERR);
            }
        });
    }

    @Override
    public Class<UpdatePatientCommand> getCommandType() {
        return UpdatePatientCommand.class;
    }
}

