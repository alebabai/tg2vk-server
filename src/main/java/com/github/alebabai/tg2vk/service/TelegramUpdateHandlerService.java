package com.github.alebabai.tg2vk.service;

import com.pengrad.telegrambot.model.Update;

public interface TelegramUpdateHandlerService {
    void handle(Update update);
    void handleAsync(Update update);
}
