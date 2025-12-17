package com.sleekydz86.api.gateway.cqrs.command;

import java.util.concurrent.CompletableFuture;

public interface CommandHandler<T extends Command, R> {
    CompletableFuture<R> handle(T command);
    Class<T> getCommandType();
}

