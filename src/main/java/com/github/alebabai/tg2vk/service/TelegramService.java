package com.github.alebabai.tg2vk.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AbstractSendRequest;

import java.util.function.Consumer;

public interface TelegramService {
    void fetchLongPollingUpdates(Consumer<? super Update> callback);

    void startWebHookUpdates();

    void stopWebHookUpdates();

    <T extends AbstractSendRequest<T>> void send(T request);
}