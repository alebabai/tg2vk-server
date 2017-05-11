package com.github.alebabai.tg2vk.service.telegram.update.command.handler;

import com.github.alebabai.tg2vk.service.telegram.update.command.TelegramCommand;

@FunctionalInterface
public interface TelegramCommandHandler {
    void handle(TelegramCommand command);
}
