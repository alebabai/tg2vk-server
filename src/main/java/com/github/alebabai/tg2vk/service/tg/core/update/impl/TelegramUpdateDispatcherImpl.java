package com.github.alebabai.tg2vk.service.tg.core.update.impl;

import com.github.alebabai.tg2vk.service.tg.core.update.TelegramUpdateDispatcher;
import com.github.alebabai.tg2vk.service.tg.core.update.TelegramUpdateMiddleware;
import com.pengrad.telegrambot.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramUpdateDispatcherImpl implements TelegramUpdateDispatcher {

    private final List<TelegramUpdateMiddleware> middlewares;

    @Autowired
    public TelegramUpdateDispatcherImpl(List<TelegramUpdateMiddleware> listeners) {
        this.middlewares = listeners;
    }

    @Override
    public void dispatch(Update update) {
        middlewares.forEach(listener -> listener.onUpdate(update));
    }
}
