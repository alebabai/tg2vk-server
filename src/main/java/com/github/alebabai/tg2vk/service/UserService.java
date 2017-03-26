package com.github.alebabai.tg2vk.service;

import com.github.alebabai.tg2vk.domain.User;

public interface UserService {
    User createOrUpdate(Integer tgId, Integer vkId, String vkToken);
}
