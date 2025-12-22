package com.sleekydz86.service.usermanagement.saga;

import java.util.concurrent.CompletableFuture;

public interface SagaStep {
    CompletableFuture<SagaStepResult> execute(Saga saga);
    CompletableFuture<SagaStepResult> compensate(Saga saga);
    String getStepName();
}

