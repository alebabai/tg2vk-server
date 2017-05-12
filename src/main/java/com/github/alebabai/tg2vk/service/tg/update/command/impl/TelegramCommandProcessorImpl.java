package com.github.alebabai.tg2vk.service.tg.update.command.impl;

import com.github.alebabai.tg2vk.service.tg.update.command.TelegramCommand;
import com.github.alebabai.tg2vk.service.tg.update.command.TelegramCommandProcessor;
import com.github.alebabai.tg2vk.service.tg.update.command.handler.TelegramCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class TelegramCommandProcessorImpl implements TelegramCommandProcessor {

    private static final String HANDLER_POSTFIX = "CommandHandler";

    private final Map<String, TelegramCommandHandler> handlersMap;

    @Autowired
    public TelegramCommandProcessorImpl(Map<String, TelegramCommandHandler> handlersMap) {
        this.handlersMap = handlersMap;
    }

    @Override
    public void process(TelegramCommand command) {
        final String handlerName = command.name() + HANDLER_POSTFIX;
        Optional.ofNullable(handlersMap.getOrDefault(handlerName, handlersMap.get("unknown" + HANDLER_POSTFIX)))
                .ifPresent(handler -> handler.handle(command));
    }
}
