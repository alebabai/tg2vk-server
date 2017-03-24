package com.github.alebabai.tg2vk.service;

import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.User;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface VkService {
    Optional<UserActor> authorize(String code);

    Optional<UserActor> authorize(Integer userId, String token);

    String getAuthorizeUrl(String redirectUrl, String... scopes);

    CompletableFuture<Integer> fetchMessages(Actor actor, BiConsumer<User, Message> consumer);
}