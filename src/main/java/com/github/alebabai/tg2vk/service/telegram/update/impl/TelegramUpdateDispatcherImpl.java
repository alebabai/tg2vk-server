package com.github.alebabai.tg2vk.service.telegram.update.impl;

import com.github.alebabai.tg2vk.service.telegram.update.TelegramUpdateDispatcher;
import com.github.alebabai.tg2vk.service.telegram.update.TelegramUpdateListener;
import com.pengrad.telegrambot.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TelegramUpdateDispatcherImpl implements TelegramUpdateDispatcher {

    private final List<TelegramUpdateListener> listeners;

    @Autowired
    public TelegramUpdateDispatcherImpl(List<TelegramUpdateListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void dispatch(Update update) {
        listeners.forEach(listener -> listener.onUpdate(update));
    }
}
