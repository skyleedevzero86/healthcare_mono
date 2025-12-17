package com.sleekydz86.api.gateway.cqrs.query;

import java.util.concurrent.CompletableFuture;

public interface QueryHandler<T extends Query, R> {
    CompletableFuture<R> handle(T query);
    Class<T> getQueryType();
}

