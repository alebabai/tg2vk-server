package com.github.alebabai.tg2vk.service.core;

import com.github.alebabai.tg2vk.domain.User;

public interface MessageFlowManager {
    void start(User user);

    void stop(User user);
}
