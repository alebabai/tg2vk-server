package com.github.alebabai.tg2vk.service;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.domain.UserSettings;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAllStarted();

    Optional<User> findOneByVkId(Integer id);

    Optional<User> findOneByTgId(Integer id);

    User save(User user);

    UserSettings updateUserSettings(UserSettings settings);

    User createOrUpdate(Integer tgId, Integer newVkId, String newVkToken);

    User create(Integer tgId, Integer vkId, String vkToken);

    Optional<ChatSettings> findChatSettings(User user, Integer vkChatId);
}
