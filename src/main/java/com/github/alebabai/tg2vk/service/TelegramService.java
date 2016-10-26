package com.github.alebabai.tg2vk.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

import java.util.function.Consumer;

public interface TelegramService {
    void fetchUpdates(Consumer<? super Update> callback);

    <T extends BaseRequest<T, R>, R extends BaseResponse> void send(T request);
}