package com.github.alebabai.tg2vk.security.service;

import com.github.alebabai.tg2vk.domain.User;

public interface JwtTokenFactoryService {

    String create(User user);
}
