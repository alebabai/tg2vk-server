package com.github.alebabai.tg2vk.service.tg.update.impl;

import com.github.alebabai.tg2vk.service.tg.update.TelegramUpdateHandler;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.ChosenInlineResult;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TelegramUpdateHandlerDefaultImpl implements TelegramUpdateHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramUpdateHandlerDefaultImpl.class);

    @Override
    public void onInlineQueryReceived(InlineQuery query) {
        LOGGER.debug("Inline query received: {}", query);
    }

    @Override
    public void onChosenInlineResultReceived(ChosenInlineResult queryResult) {
        LOGGER.debug("Chosen inline result received: {}", queryResult);
    }

    @Override
    public void onCallbackQueryReceived(CallbackQuery callbackQuery) {
        LOGGER.debug("Callback query received: {}", callbackQuery);
    }

    @Override
    public void onChanelPostReceived(Message post) {
        LOGGER.debug("Chanel post received: {}", post);
    }

    @Override
    public void onEditedChanelPostReceived(Message post) {
        LOGGER.debug("Edited chanel post received: {}", post);
    }

    @Override
    public void onEditedMessageReceived(Message message) {
        LOGGER.debug("Edited message received: {}", message);
    }

    @Override
    public void onMessageReceived(Message message) {
        LOGGER.debug("Message received: {}", message);
    }
}
