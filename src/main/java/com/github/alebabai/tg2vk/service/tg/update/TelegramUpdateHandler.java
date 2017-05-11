package com.github.alebabai.tg2vk.service.tg.update;

import com.pengrad.telegrambot.model.Update;

public interface TelegramUpdateHandler {
    void handle(Update update);
}
