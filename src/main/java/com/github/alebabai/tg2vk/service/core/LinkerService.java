package com.github.alebabai.tg2vk.service.core;

import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.User;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface LinkerService {

    BiConsumer<User, Message> getVkMessageHandler(Integer userId);
}
