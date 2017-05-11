package com.github.alebabai.tg2vk.service.telegram.update.impl;

import com.github.alebabai.tg2vk.service.telegram.update.TelegramUpdateDispatcher;
import com.github.alebabai.tg2vk.service.telegram.update.TelegramUpdateMiddleware;
import com.pengrad.telegrambot.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramUpdateDispatcherImpl implements TelegramUpdateDispatcher {

    private final List<TelegramUpdateMiddleware> middlewareList;

    @Autowired
    public TelegramUpdateDispatcherImpl(List<TelegramUpdateMiddleware> listeners) {
        this.middlewareList = listeners;
    }

    @Override
    public void dispatch(Update update) {
        middlewareList.forEach(listener -> listener.onUpdate(update));
    }
}
