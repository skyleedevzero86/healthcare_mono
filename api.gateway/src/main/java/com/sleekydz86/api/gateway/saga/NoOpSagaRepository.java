package com.sleekydz86.api.gateway.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@ConditionalOnProperty(name = "spring.datasource.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpSagaRepository implements SagaRepository {

    @Override
    public void save(Saga saga) {
        log.warn("Database is not enabled. Saga save operation ignored: {}", saga.getSagaId());
    }

    @Override
    public Optional<Saga> findById(UUID sagaId) {
        log.warn("Database is not enabled. Saga findById operation ignored: {}", sagaId);
        return Optional.empty();
    }

    @Override
    public Optional<Saga> findBySagaType(String sagaType) {
        log.warn("Database is not enabled. Saga findBySagaType operation ignored: {}", sagaType);
        return Optional.empty();
    }
}

