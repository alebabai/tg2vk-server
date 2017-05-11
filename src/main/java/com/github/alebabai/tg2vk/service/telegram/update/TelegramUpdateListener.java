package com.github.alebabai.tg2vk.service.telegram.update;

import com.pengrad.telegrambot.model.Update;

@FunctionalInterface
public interface TelegramUpdateListener {
    void onUpdate(Update update);
}
