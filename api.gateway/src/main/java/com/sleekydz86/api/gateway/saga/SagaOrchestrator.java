package com.sleekydz86.api.gateway.saga;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SagaOrchestrator {

    private final SagaRepository sagaRepository;
    private final List<SagaStep> sagaSteps;
    private final ObjectMapper objectMapper;
    
    @Value("${saga.execution.timeout.seconds:300}")
    private int executionTimeoutSeconds;
    
    @Value("${saga.retry.maxAttempts:3}")
    private int maxRetryAttempts;
    
    @Value("${saga.retry.delay.seconds:5}")
    private int retryDelaySeconds;

    public SagaOrchestrator(SagaRepository sagaRepository, List<SagaStep> sagaSteps, ObjectMapper objectMapper) {
        this.sagaRepository = sagaRepository;
        this.sagaSteps = sagaSteps;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<SagaResult> executeSaga(Saga saga) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                saga.setStatus(SagaStatus.STARTED);
                sagaRepository.save(saga);
                
                List<SagaStep> executedSteps = new ArrayList<>();
                List<SagaStep> orderedSteps = getOrderedSteps(saga.getSagaType());
                
                for (SagaStep step : orderedSteps) {
                    SagaStepResult result = executeStepWithRetry(step, saga);
                    
                    if (!result.isSuccess()) {
                        log.error("Saga 단계 실패: {} for saga: {}", step.getStepName(), saga.getSagaId());
                        return compensateSaga(saga, executedSteps);
                    }
                    
                    executedSteps.add(step);
                    sagaRepository.save(saga);
                }

                saga.setStatus(SagaStatus.COMPLETED);
                sagaRepository.save(saga);
                
                log.info("Saga 완료: {}", saga.getSagaId());
                return new SagaResult(true, "Saga completed successfully", saga);

            } catch (Exception e) {
                log.error("Saga 실행 실패: {}", saga.getSagaId(), e);
                saga.setStatus(SagaStatus.FAILED);
                sagaRepository.save(saga);
                return new SagaResult(false, "Saga failed: " + e.getMessage(), saga);
            }
        });
    }

    private SagaStepResult executeStepWithRetry(SagaStep step, Saga saga) {
        int attempt = 0;
        Exception lastException = null;
        
        while (attempt < maxRetryAttempts) {
            try {
                CompletableFuture<SagaStepResult> future = step.execute(saga);
                SagaStepResult result = future.get(executionTimeoutSeconds, TimeUnit.SECONDS);
                
                if (result.isSuccess()) {
                    return result;
                }
                
                lastException = new RuntimeException(result.getMessage());
                attempt++;
                
                if (attempt < maxRetryAttempts) {
                    log.warn("단계 {} 실패, 재시도 중 (시도 {}/{})", step.getStepName(), attempt, maxRetryAttempts);
                    Thread.sleep(retryDelaySeconds * 1000L);
                }
                
            } catch (java.util.concurrent.TimeoutException e) {
                log.error("단계 {} 타임아웃: {} 초 경과", step.getStepName(), executionTimeoutSeconds);
                lastException = e;
                attempt++;
                
                if (attempt < maxRetryAttempts) {
                    try {
                        Thread.sleep(retryDelaySeconds * 1000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                log.error("단계 {} 실행 오류", step.getStepName(), e);
                lastException = e;
                attempt++;
                
                if (attempt < maxRetryAttempts) {
                    try {
                        Thread.sleep(retryDelaySeconds * 1000L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        return new SagaStepResult(false, 
            "Step failed after " + maxRetryAttempts + " attempts: " + 
            (lastException != null ? lastException.getMessage() : "Unknown error"), null);
    }

    private SagaResult compensateSaga(Saga saga, List<SagaStep> executedSteps) {
        try {
            saga.setStatus(SagaStatus.COMPENSATING);
            sagaRepository.save(saga);
            
            log.info("Saga 보상 트랜잭션 시작: {}", saga.getSagaId());
            
            for (int i = executedSteps.size() - 1; i >= 0; i--) {
                SagaStep step = executedSteps.get(i);
                
                try {
                    CompletableFuture<SagaStepResult> compensateFuture = step.compensate(saga);
                    SagaStepResult result = compensateFuture.get(executionTimeoutSeconds, TimeUnit.SECONDS);
                    
                    if (!result.isSuccess()) {
                        log.error("보상 트랜잭션 실패 - 단계: {}, Saga: {}", 
                            step.getStepName(), saga.getSagaId());
                    } else {
                        log.info("보상 트랜잭션 성공 - 단계: {}, Saga: {}", 
                            step.getStepName(), saga.getSagaId());
                    }
                } catch (Exception e) {
                    log.error("보상 트랜잭션 오류 - 단계: {}, Saga: {}", 
                        step.getStepName(), saga.getSagaId(), e);
                }
            }

            saga.setStatus(SagaStatus.COMPENSATED);
            sagaRepository.save(saga);
            
            log.info("Saga 보상 트랜잭션 완료: {}", saga.getSagaId());
            return new SagaResult(false, "Saga compensated", saga);

        } catch (Exception e) {
            log.error("Saga 보상 트랜잭션 실패: {}", saga.getSagaId(), e);
            saga.setStatus(SagaStatus.FAILED);
            sagaRepository.save(saga);
            return new SagaResult(false, "Saga compensation failed: " + e.getMessage(), saga);
        }
    }
    
    private List<SagaStep> getOrderedSteps(String sagaType) {
        if ("PatientRegistration".equals(sagaType)) {
            return sagaSteps.stream()
                .filter(step -> step.getStepName().equals("CreatePatient") || 
                               step.getStepName().equals("CreateUserAccount") || 
                               step.getStepName().equals("SendWelcomeNotification"))
                .sorted((s1, s2) -> {
                    int order1 = getStepOrder(s1.getStepName());
                    int order2 = getStepOrder(s2.getStepName());
                    return Integer.compare(order1, order2);
                })
                .collect(Collectors.toList());
        }
        return sagaSteps;
    }
    
    private int getStepOrder(String stepName) {
        switch (stepName) {
            case "CreatePatient": return 1;
            case "CreateUserAccount": return 2;
            case "SendWelcomeNotification": return 3;
            default: return 999;
        }
    }
    
    public CompletableFuture<SagaResult> resumeSaga(UUID sagaId) {
        return CompletableFuture.supplyAsync(() -> {
            return sagaRepository.findById(sagaId)
                .map(saga -> {
                    if (saga.getStatus() == SagaStatus.STARTED || 
                        saga.getStatus() == SagaStatus.COMPENSATING) {
                        log.info("Saga 재개: {}", sagaId);
                        return executeSaga(saga).join();
                    } else {
                        log.warn("Saga 재개 불가: {} 상태: {}", sagaId, saga.getStatus());
                        return new SagaResult(false, 
                            "Saga 재개 불가 상태: " + saga.getStatus(), saga);
                    }
                })
                .orElse(new SagaResult(false, "Saga를 찾을 수 없음: " + sagaId, null));
        });
    }
    
    public CompletableFuture<SagaStatus> getSagaStatus(UUID sagaId) {
        return CompletableFuture.supplyAsync(() -> {
            return sagaRepository.findById(sagaId)
                .map(Saga::getStatus)
                .orElse(null);
        });
    }
}
