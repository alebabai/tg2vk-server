package com.github.alebabai.tg2vk.service;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.domain.UserSettings;
import com.github.alebabai.tg2vk.exception.UserCreationException;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Stream;

public interface UserService {
    Stream<User> findAllStarted();

    Optional<User> findOneByVkId(Integer id);

    Optional<User> findOneByTgId(Integer id);

    User save(User user);

    UserSettings updateUserSettings(UserSettings settings);

    User createOrUpdate(Integer tgId, Integer newVkId, String newVkToken) throws UserCreationException;

    User create(Integer tgId, Integer vkId, String vkToken) throws UserCreationException;

    Optional<ChatSettings> findChatSettings(User user, Integer vkChatId);
}
