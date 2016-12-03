package com.github.alebabai.tg2vk.service;

import com.github.alebabai.tg2vk.domain.User;

public interface LinkerService {
    void start(User user);
    void stop(User user);
}
