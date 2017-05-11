package com.github.alebabai.tg2vk.service.telegram.update.impl;

import com.github.alebabai.tg2vk.service.telegram.update.TelegramUpdateHandler;
import com.github.alebabai.tg2vk.service.telegram.update.TelegramUpdateMiddleware;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramUpdateDestructorMiddleware implements TelegramUpdateMiddleware {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramUpdateDestructorMiddleware.class);

    private final List<TelegramUpdateHandler> handlers;

    @Autowired
    public TelegramUpdateDestructorMiddleware(List<TelegramUpdateHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void onUpdate(Update update) {
        handlers.forEach(handler -> destructUpdate(update, handler));
    }

    private void destructUpdate(Update update, TelegramUpdateHandler handler) {
        if (update == null) {
            LOGGER.debug("Can't handle empty update");
            return;
        }

        if (update.inlineQuery() != null) {
            handler.onInlineQueryReceived(update.inlineQuery());
        }

        if (update.chosenInlineResult() != null) {
            handler.onChosenInlineResultReceived(update.chosenInlineResult());
        }

        if (update.callbackQuery() != null) {
            handler.onCallbackQueryReceived(update.callbackQuery());
        }

        if (update.channelPost() != null) {
            handler.onChanelPostReceived(update.channelPost());
        }

        if (update.editedChannelPost() != null) {
            handler.onEditedChanelPostReceived(update.editedChannelPost());
        }

        if (update.message() != null) {
            handler.onMessageReceived(update.message());
        }

        if (update.editedMessage() != null) {
            handler.onEditedMessageReceived(update.editedMessage());
        }
    }
}
