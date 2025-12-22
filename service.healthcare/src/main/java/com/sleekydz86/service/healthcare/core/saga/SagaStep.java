package com.sleekydz86.service.healthcare.core.saga;

import java.util.concurrent.CompletableFuture;

public interface SagaStep {
    CompletableFuture<SagaStepResult> execute(Saga saga);
    CompletableFuture<SagaStepResult> compensate(Saga saga);
    String getStepName();
}

