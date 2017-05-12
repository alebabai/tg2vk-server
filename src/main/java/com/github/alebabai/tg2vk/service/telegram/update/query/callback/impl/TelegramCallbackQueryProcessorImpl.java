package com.github.alebabai.tg2vk.service.telegram.update.query.callback.impl;

import com.github.alebabai.tg2vk.service.telegram.update.query.callback.TelegramCallbackQuery;
import com.github.alebabai.tg2vk.service.telegram.update.query.callback.TelegramCallbackQueryData;
import com.github.alebabai.tg2vk.service.telegram.update.query.callback.TelegramCallbackQueryHandler;
import com.github.alebabai.tg2vk.service.telegram.update.query.callback.TelegramCallbackQueryProcessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.model.CallbackQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class TelegramCallbackQueryProcessorImpl implements TelegramCallbackQueryProcessor {

    private static final String HANDLER_POSTFIX = "CallbackQueryHandler";

    private final Map<String, TelegramCallbackQueryHandler> handlersMap;
    private final Gson gson;

    @Autowired
    public TelegramCallbackQueryProcessorImpl(Map<String, TelegramCallbackQueryHandler> handlersMap) {
        this.handlersMap = handlersMap;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void process(CallbackQuery query) {
        final TelegramCallbackQueryData data = gson.fromJson(query.data(), TelegramCallbackQueryData.class);
        final String handlerName = data.type() + HANDLER_POSTFIX;
        Optional.ofNullable(handlersMap.get(handlerName))
                .ifPresent(handler -> handler.handle(new TelegramCallbackQuery(data, query)));
    }
}
