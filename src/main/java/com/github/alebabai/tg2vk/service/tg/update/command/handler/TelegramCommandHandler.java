package com.github.alebabai.tg2vk.service.tg.update.command.handler;

import com.github.alebabai.tg2vk.service.tg.update.command.TelegramCommand;

@FunctionalInterface
public interface TelegramCommandHandler {
    void handle(TelegramCommand command);
}
