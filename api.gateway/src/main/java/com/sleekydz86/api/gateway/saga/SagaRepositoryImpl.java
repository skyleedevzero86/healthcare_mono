package com.sleekydz86.api.gateway.saga;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SagaRepositoryImpl implements SagaRepository {

    private final ConcurrentHashMap<UUID, Saga> sagaStore = new ConcurrentHashMap<>();

    @Override
    public void save(Saga saga) {
        sagaStore.put(saga.getSagaId(), saga);
    }

    @Override
    public Optional<Saga> findById(UUID sagaId) {
        return Optional.ofNullable(sagaStore.get(sagaId));
    }

    @Override
    public Optional<Saga> findBySagaType(String sagaType) {
        return sagaStore.values().stream()
            .filter(saga -> saga.getSagaType().equals(sagaType))
            .findFirst();
    }
}

