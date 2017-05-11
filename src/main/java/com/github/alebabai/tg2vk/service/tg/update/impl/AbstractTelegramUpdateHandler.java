package com.github.alebabai.tg2vk.service.tg.update.impl;


import com.github.alebabai.tg2vk.service.tg.update.TelegramUpdateHandler;
import com.pengrad.telegrambot.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTelegramUpdateHandler implements TelegramUpdateHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTelegramUpdateHandler.class);

    @Override
    public void handle(Update update) {
        mainHandler(update);
    }

    private void mainHandler(Update update) {
        if (update == null) {
            LOGGER.debug("Can't handle empty update");
            return;
        }

        if (update.inlineQuery() != null) {
            onInlineQueryReceived(update.inlineQuery());
        }

        if (update.chosenInlineResult() != null) {
            onChosenInlineResultReceived(update.chosenInlineResult());
        }

        if (update.callbackQuery() != null) {
            onCallbackQueryReceived(update.callbackQuery());
        }

        if (update.channelPost() != null) {
            onChanelPostReceived(update.channelPost());
        }

        if (update.editedChannelPost() != null) {
            onEditedChanelPostReceived(update.editedChannelPost());
        }

        if (update.message() != null) {
            onMessageReceived(update.message());
        }

        if (update.editedMessage() != null) {
            onEditedMessageReceived(update.editedMessage());
        }
    }

    protected abstract void onInlineQueryReceived(InlineQuery query);

    protected abstract void onChosenInlineResultReceived(ChosenInlineResult queryResult);

    protected abstract void onCallbackQueryReceived(CallbackQuery callbackQuery);

    protected abstract void onChanelPostReceived(Message post);

    protected abstract void onEditedChanelPostReceived(Message post);

    protected abstract void onEditedMessageReceived(Message message);

    protected abstract void onMessageReceived(Message message);
}
