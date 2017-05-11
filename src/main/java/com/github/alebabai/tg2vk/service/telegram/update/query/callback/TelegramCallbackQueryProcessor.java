package com.github.alebabai.tg2vk.service.telegram.update.query.callback;

import com.pengrad.telegrambot.model.CallbackQuery;

@FunctionalInterface
public interface TelegramCallbackQueryProcessor {
    void process(CallbackQuery query);
}
