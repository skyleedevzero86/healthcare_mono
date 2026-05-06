package com.sleekydz86.api.gateway.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@ConditionalOnProperty(name = "spring.datasource.enabled", havingValue = "true", matchIfMissing = false)
public class SagaRepositoryImpl implements SagaRepository {

    private final SagaMapper sagaMapper;
    private final ObjectMapper objectMapper;

    public SagaRepositoryImpl(SagaMapper sagaMapper, ObjectMapper objectMapper) {
        this.sagaMapper = sagaMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void save(Saga saga) {
        try {
            String sagaId = saga.getSagaId().toString();
            String sagaType = saga.getSagaType();
            String status = saga.getStatus().name();
            String dataJson = serializeData(saga.getData());

            int count = sagaMapper.countBySagaId(sagaId);

            SagaEntity sagaEntity = SagaEntity.builder()
                    .sagaId(sagaId)
                    .sagaType(sagaType)
                    .status(status)
                    .data(dataJson)
                    .updatedAt(LocalDateTime.now())
                    .build();

            if (count > 0) {
                sagaMapper.update(sagaEntity);
            } else {
                sagaEntity.setCreatedAt(LocalDateTime.now());
                sagaMapper.insert(sagaEntity);
            }

            log.debug("Saga saved: {}", sagaId);
        } catch (Exception e) {
            log.error("Saga 저장 실패: {}", saga.getSagaId(), e);
            throw new RuntimeException("Saga 저장 실패", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Saga> findById(UUID sagaId) {
        try {
            SagaEntity entity = sagaMapper.selectById(sagaId.toString());
            if (entity == null) {
                return Optional.empty();
            }

            Saga saga = createSagaInstance(entity.getSagaType(), UUID.fromString(entity.getSagaId()));
            saga.setStatus(SagaStatus.valueOf(entity.getStatus()));
            saga.setData(deserializeData(entity.getSagaType(), entity.getData()));

            return Optional.of(saga);
        } catch (Exception e) {
            log.error("Saga 조회 실패: {}", sagaId, e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Saga> findBySagaType(String sagaType) {
        try {
            SagaEntity entity = sagaMapper.selectBySagaType(sagaType);
            if (entity == null) {
                return Optional.empty();
            }

            Saga saga = createSagaInstance(entity.getSagaType(), UUID.fromString(entity.getSagaId()));
            saga.setStatus(SagaStatus.valueOf(entity.getStatus()));
            saga.setData(deserializeData(entity.getSagaType(), entity.getData()));

            return Optional.of(saga);
        } catch (Exception e) {
            log.error("Saga 타입별 조회 실패: {}", sagaType, e);
            return Optional.empty();
        }
    }

    private Saga createSagaInstance(String sagaType, UUID sagaId) {
        if ("PatientRegistration".equals(sagaType)) {
            return new SagaImpl(sagaId, sagaType, SagaStatus.STARTED, null);
        }
        throw new IllegalArgumentException("알 수 없는 Saga 타입: " + sagaType);
    }

    private static class SagaImpl implements Saga {
        private final UUID sagaId;
        private final String sagaType;
        private SagaStatus status;
        private Object data;

        public SagaImpl(UUID sagaId, String sagaType, SagaStatus status, Object data) {
            this.sagaId = sagaId;
            this.sagaType = sagaType;
            this.status = status;
            this.data = data;
        }

        @Override
        public UUID getSagaId() {
            return sagaId;
        }

        @Override
        public String getSagaType() {
            return sagaType;
        }

        @Override
        public SagaStatus getStatus() {
            return status;
        }

        @Override
        public void setStatus(SagaStatus status) {
            this.status = status;
        }

        @Override
        public Object getData() {
            return data;
        }

        @Override
        public void setData(Object data) {
            this.data = data;
        }
    }

    private String serializeData(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("Saga 데이터 직렬화 실패", e);
            return "{}";
        }
    }

    private Object deserializeData(String sagaType, String dataJson) {
        try {
            return objectMapper.readValue(dataJson, Object.class);
        } catch (Exception e) {
            log.error("Saga 데이터 역직렬화 실패", e);
            return null;
        }
    }
}
