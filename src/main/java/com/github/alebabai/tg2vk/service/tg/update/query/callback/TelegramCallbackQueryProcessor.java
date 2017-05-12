package com.github.alebabai.tg2vk.service.tg.update.query.callback;

import com.pengrad.telegrambot.model.CallbackQuery;

@FunctionalInterface
public interface TelegramCallbackQueryProcessor {
    void process(CallbackQuery query);
}
