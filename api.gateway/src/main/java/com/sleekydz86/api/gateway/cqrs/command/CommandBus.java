package com.sleekydz86.api.gateway.cqrs.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class CommandBus {

    @Autowired
    private List<CommandHandler> commandHandlers;

    public <T extends Command, R> CompletableFuture<R> send(T command) {
        CommandHandler<T, R> handler = findHandler(command);
        if (handler == null) {
            throw new RuntimeException("커맨드 핸들러를 찾을 수 없습니다: " + command.getCommandType());
        }
        return handler.handle(command);
    }

    @SuppressWarnings("unchecked")
    private <T extends Command, R> CommandHandler<T, R> findHandler(T command) {
        return commandHandlers.stream()
            .filter(handler -> handler.getCommandType().equals(command.getClass()))
            .map(handler -> (CommandHandler<T, R>) handler)
            .findFirst()
            .orElse(null);
    }
}

