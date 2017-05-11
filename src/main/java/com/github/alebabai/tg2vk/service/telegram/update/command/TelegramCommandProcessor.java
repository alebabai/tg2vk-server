package com.github.alebabai.tg2vk.service.telegram.update.command;

@FunctionalInterface
public interface TelegramCommandProcessor {
    void process(TelegramCommand command);
}
