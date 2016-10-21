package com.github.alebabai.tg2vk.service;


import com.vk.api.sdk.client.actors.UserActor;

public interface VkService {
    UserActor authorize(String code);
    boolean isAuthorized();
}