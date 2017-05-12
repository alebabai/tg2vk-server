package com.github.alebabai.tg2vk.service.tg.update.command;

@FunctionalInterface
public interface TelegramCommandProcessor {
    void process(TelegramCommand command);
}
