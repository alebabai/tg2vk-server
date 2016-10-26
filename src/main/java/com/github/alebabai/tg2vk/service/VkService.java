package com.github.alebabai.tg2vk.service;


import com.vk.api.sdk.objects.messages.Message;

import java.util.function.Consumer;
import java.util.function.Function;

public interface VkService {
    void authorize(String code);

    boolean isAuthorized();

    String getAuthorizeUrl(String redirectUrl, String... scopes);

    void fetchMessages(Consumer<? super Message> callback);
}