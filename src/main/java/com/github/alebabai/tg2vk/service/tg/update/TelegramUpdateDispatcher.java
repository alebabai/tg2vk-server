package com.github.alebabai.tg2vk.service.tg.update;

import com.pengrad.telegrambot.model.Update;

@FunctionalInterface
public interface TelegramUpdateDispatcher {
    void dispatch(Update update);
}
