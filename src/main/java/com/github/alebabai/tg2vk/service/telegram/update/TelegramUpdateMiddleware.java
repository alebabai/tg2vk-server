package com.github.alebabai.tg2vk.service.telegram.update;

import com.pengrad.telegrambot.model.Update;

@FunctionalInterface
public interface TelegramUpdateMiddleware {
    void onUpdate(Update update);
}
