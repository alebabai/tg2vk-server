package com.github.alebabai.tg2vk.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

import java.util.function.Consumer;

public interface TelegramService {
    void fetchLongPollingUpdates(Consumer<? super Update> callback);

    void startWebHookUpdates();

    void stopWebHookUpdates();

    <T extends AbstractSendRequest<T>> void send(T request);
}