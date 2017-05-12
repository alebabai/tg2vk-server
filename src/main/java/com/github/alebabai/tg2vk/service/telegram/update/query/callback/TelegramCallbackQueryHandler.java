package com.github.alebabai.tg2vk.service.telegram.update.query.callback;

@FunctionalInterface
public interface TelegramCallbackQueryHandler {
    void handle(TelegramCallbackQuery query);
}
