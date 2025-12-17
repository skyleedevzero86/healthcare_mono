package com.sleekydz86.api.gateway.cqrs.query;

import com.sleekydz86.api.gateway.dto.ApiResultCode;
import com.sleekydz86.api.gateway.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class QueryBus {

    private final List<QueryHandler> queryHandlers;

    public QueryBus(List<QueryHandler> queryHandlers) {
        this.queryHandlers = queryHandlers;
    }

    public <T extends Query, R> CompletableFuture<R> send(T query) {
        QueryHandler<T, R> handler = findHandler(query);
        if (handler == null) {
            throw new BusinessException("쿼리 핸들러를 찾을 수 없습니다: " + query.getQueryType(), ApiResultCode.INTERNAL_ERROR);
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
