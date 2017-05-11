package com.github.alebabai.tg2vk.service;

import com.github.alebabai.tg2vk.domain.Chat;
import com.github.alebabai.tg2vk.domain.User;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.messages.Message;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public interface VkService {
    Optional<UserActor> authorize(String code);

    Optional<UserActor> authorize(Integer userId, String token);

    String getAuthorizeUrl(String redirectUrl, String... scopes);

    int fetchMessages(User user, BiConsumer<com.vk.api.sdk.objects.users.User, Message> consumer);

    List<Chat> findChats(User user, String query);

    List<Chat> resolveChats(User user);
}