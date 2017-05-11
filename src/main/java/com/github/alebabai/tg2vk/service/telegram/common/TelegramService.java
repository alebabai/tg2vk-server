package com.github.alebabai.tg2vk.service.telegram.common;

import com.github.alebabai.tg2vk.domain.Chat;
import com.github.alebabai.tg2vk.domain.User;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;

import java.util.List;
import java.util.function.Consumer;

public interface TelegramService {
    void startLongPollingUpdates(Consumer<Update> callback);

    void stopLongPollingUpdates();

    void startWebHookUpdates();

    void stopWebHookUpdates();

    void send(BaseRequest request);

    List<Chat> resolveChats(User user);
}