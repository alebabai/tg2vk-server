package com.github.alebabai.tg2vk.service;


public interface VkService {
    void authorize(String code);
    boolean isAuthorized();
    String getAuthorizeUrl(boolean allowAll);
}