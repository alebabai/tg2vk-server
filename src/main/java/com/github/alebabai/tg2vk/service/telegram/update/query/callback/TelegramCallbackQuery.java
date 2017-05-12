package com.github.alebabai.tg2vk.service.telegram.update.query.callback;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@RequiredArgsConstructor
public class TelegramCallbackQuery {
    private final TelegramCallbackQueryData data;
    private final CallbackQuery context;
}
