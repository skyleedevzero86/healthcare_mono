package com.sleekydz86.api.gateway.cqrs.command;

import com.sleekydz86.api.gateway.dto.ApiResultCode;
import com.sleekydz86.api.gateway.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class CommandBus {

    private final List<CommandHandler> commandHandlers;

    public CommandBus(List<CommandHandler> commandHandlers) {
        this.commandHandlers = commandHandlers;
    }

    public <T extends Command, R> CompletableFuture<R> send(T command) {
        CommandHandler<T, R> handler = findHandler(command);
        if (handler == null) {
            throw new BusinessException("커맨드 핸들러를 찾을 수 없습니다: " + command.getCommandType(),
                    ApiResultCode.INTERNAL_ERROR);
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
