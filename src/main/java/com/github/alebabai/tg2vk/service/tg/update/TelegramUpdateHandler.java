package com.github.alebabai.tg2vk.service.tg.update;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.ChosenInlineResult;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;

public interface TelegramUpdateHandler {
    default void onInlineQueryReceived(InlineQuery query) {}

    default void onChosenInlineResultReceived(ChosenInlineResult queryResult) {}

    default void onCallbackQueryReceived(CallbackQuery callbackQuery) {}

    default void onChanelPostReceived(Message post) {}

    default void onEditedChanelPostReceived(Message post) {}

    default void onEditedMessageReceived(Message message) {}

    default void onMessageReceived(Message message) {}
}
