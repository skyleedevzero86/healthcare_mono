package com.sleekydz86.api.gateway.saga;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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

    @PersistenceContext
    private EntityManager entityManager;

    private final ObjectMapper objectMapper;

    public SagaRepositoryImpl(ObjectMapper objectMapper) {
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

            Query checkQuery = entityManager.createNativeQuery(
                    "SELECT COUNT(*) FROM sagas WHERE saga_id = :sagaId");
            checkQuery.setParameter("sagaId", sagaId);
            Long count = ((Number) checkQuery.getSingleResult()).longValue();

            if (count > 0) {
                Query updateQuery = entityManager.createNativeQuery(
                        "UPDATE sagas SET saga_type = :sagaType, status = :status, " +
                                "data = :data, updated_at = :updatedAt WHERE saga_id = :sagaId");
                updateQuery.setParameter("sagaId", sagaId);
                updateQuery.setParameter("sagaType", sagaType);
                updateQuery.setParameter("status", status);
                updateQuery.setParameter("data", dataJson);
                updateQuery.setParameter("updatedAt", LocalDateTime.now());
                updateQuery.executeUpdate();
            } else {
                Query insertQuery = entityManager.createNativeQuery(
                        "INSERT INTO sagas (saga_id, saga_type, status, data, created_at, updated_at) " +
                                "VALUES (:sagaId, :sagaType, :status, :data, :createdAt, :updatedAt)");
                insertQuery.setParameter("sagaId", sagaId);
                insertQuery.setParameter("sagaType", sagaType);
                insertQuery.setParameter("status", status);
                insertQuery.setParameter("data", dataJson);
                insertQuery.setParameter("createdAt", LocalDateTime.now());
                insertQuery.setParameter("updatedAt", LocalDateTime.now());
                insertQuery.executeUpdate();
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
            Query query = entityManager.createNativeQuery(
                    "SELECT saga_id, saga_type, status, data FROM sagas WHERE saga_id = :sagaId");
            query.setParameter("sagaId", sagaId.toString());

            Object[] result = (Object[]) query.getSingleResult();
            if (result == null) {
                return Optional.empty();
            }

            String sagaType = (String) result[1];
            String statusStr = (String) result[2];
            String dataJson = (String) result[3];

            Saga saga = createSagaInstance(sagaType, UUID.fromString((String) result[0]));
            saga.setStatus(SagaStatus.valueOf(statusStr));
            saga.setData(deserializeData(sagaType, dataJson));

            return Optional.of(saga);
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            log.error("Saga 조회 실패: {}", sagaId, e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Saga> findBySagaType(String sagaType) {
        try {
            Query query = entityManager.createNativeQuery(
                    "SELECT saga_id, saga_type, status, data FROM sagas " +
                            "WHERE saga_type = :sagaType ORDER BY created_at DESC LIMIT 1");
            query.setParameter("sagaType", sagaType);

            Object[] result = (Object[]) query.getSingleResult();
            if (result == null) {
                return Optional.empty();
            }

            String statusStr = (String) result[2];
            String dataJson = (String) result[3];

            Saga saga = createSagaInstance(sagaType, UUID.fromString((String) result[0]));
            saga.setStatus(SagaStatus.valueOf(statusStr));
            saga.setData(deserializeData(sagaType, dataJson));

            return Optional.of(saga);
        } catch (jakarta.persistence.NoResultException e) {
            return Optional.empty();
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
