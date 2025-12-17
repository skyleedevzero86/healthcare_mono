package com.sleekydz86.api.gateway.cqrs.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class QueryBus {

    @Autowired
    private List<QueryHandler> queryHandlers;

    public <T extends Query, R> CompletableFuture<R> send(T query) {
        QueryHandler<T, R> handler = findHandler(query);
        if (handler == null) {
            throw new RuntimeException("쿼리 핸들러를 찾을 수 없습니다: " + query.getQueryType());
        }
        return handler.handle(query);
    }

    @SuppressWarnings("unchecked")
    private <T extends Query, R> QueryHandler<T, R> findHandler(T query) {
        return queryHandlers.stream()
            .filter(handler -> handler.getQueryType().equals(query.getClass()))
            .map(handler -> (QueryHandler<T, R>) handler)
            .findFirst()
            .orElse(null);
    }
}

