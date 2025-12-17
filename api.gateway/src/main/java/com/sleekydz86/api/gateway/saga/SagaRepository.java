package com.sleekydz86.api.gateway.saga;

import java.util.UUID;
import java.util.Optional;

public interface SagaRepository {
    void save(Saga saga);
    Optional<Saga> findById(UUID sagaId);
    Optional<Saga> findBySagaType(String sagaType);
}

