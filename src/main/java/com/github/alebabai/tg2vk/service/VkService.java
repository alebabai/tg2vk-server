package com.github.alebabai.tg2vk.service;

import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.User;

import java.util.function.BiConsumer;

public interface VkService {
    void authorize(String code);

    boolean isAuthorized();

    String getAuthorizeUrl(String redirectUrl, String... scopes);

    void fetchMessages(BiConsumer<? super User, ? super Message> callback);
}