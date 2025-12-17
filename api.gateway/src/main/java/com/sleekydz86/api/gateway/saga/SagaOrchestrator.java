package com.sleekydz86.api.gateway.saga;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class SagaOrchestrator {

    private final SagaRepository sagaRepository;
    private final List<SagaStep> sagaSteps;

    public SagaOrchestrator(SagaRepository sagaRepository, List<SagaStep> sagaSteps) {
        this.sagaRepository = sagaRepository;
        this.sagaSteps = sagaSteps;
    }

    public CompletableFuture<SagaResult> executeSaga(Saga saga) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                saga.setStatus(SagaStatus.STARTED);
                sagaRepository.save(saga);

                List<SagaStep> executedSteps = new ArrayList<>();
                for (SagaStep step : sagaSteps) {
                    SagaStepResult result = step.execute(saga).get();

                    if (!result.isSuccess()) {
                        return compensateSaga(saga, executedSteps);
                    }
                    executedSteps.add(step);
                }

                saga.setStatus(SagaStatus.COMPLETED);
                sagaRepository.save(saga);

                return new SagaResult(true, "Saga completed successfully", saga);

            } catch (Exception e) {
                saga.setStatus(SagaStatus.FAILED);
                sagaRepository.save(saga);
                return new SagaResult(false, "Saga failed: " + e.getMessage(), saga);
            }
        });
    }

    private SagaResult compensateSaga(Saga saga, List<SagaStep> executedSteps) {
        try {
            saga.setStatus(SagaStatus.COMPENSATING);
            sagaRepository.save(saga);

            for (int i = executedSteps.size() - 1; i >= 0; i--) {
                SagaStep step = executedSteps.get(i);
                SagaStepResult result = step.compensate(saga).get();

                if (!result.isSuccess()) {
                    System.err.println("Compensation failed for step: " + step.getStepName());
                }
            }

            saga.setStatus(SagaStatus.COMPENSATED);
            sagaRepository.save(saga);

            return new SagaResult(false, "Saga compensated", saga);

        } catch (Exception e) {
            saga.setStatus(SagaStatus.FAILED);
            sagaRepository.save(saga);
            return new SagaResult(false, "Saga compensation failed: " + e.getMessage(), saga);
        }
    }
}

