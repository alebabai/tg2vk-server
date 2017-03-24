package com.github.alebabai.tg2vk.service;

import com.github.alebabai.tg2vk.domain.User;
import com.vk.api.sdk.objects.messages.Message;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface LinkerService {
    BiConsumer<com.vk.api.sdk.objects.users.User, Message> getVkMessageHandler(User user);
}
