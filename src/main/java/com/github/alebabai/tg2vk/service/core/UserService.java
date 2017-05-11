package com.github.alebabai.tg2vk.service.core;

import com.github.alebabai.tg2vk.domain.User;

public interface UserService {
    User createOrUpdate(Integer tgId, Integer vkId, String vkToken);
}
