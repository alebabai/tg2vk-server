package com.github.alebabai.tg2vk.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;

import java.util.function.Consumer;

public interface TelegramService {
    void startLongPollingUpdates(Consumer<Update> callback);

    void stopLongPollingUpdates();

    void startWebHookUpdates();

    void stopWebHookUpdates();

    void send(BaseRequest request);
}