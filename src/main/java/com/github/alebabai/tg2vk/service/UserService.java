package com.github.alebabai.tg2vk.service;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.exception.UserCreationException;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserService {
    Stream<User> findAllStarted();

    Optional<User> findOneByVkId(Integer id);

    Optional<User> findOneByTgId(Integer id);

    User save(User user);

    User createOrUpdate(Integer tgId, Integer newVkId, String newVkToken) throws UserCreationException;

    User create(Integer tgId, Integer vkId, String vkToken) throws UserCreationException;
}
