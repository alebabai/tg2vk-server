package com.github.alebabai.tg2vk.service;

import com.github.alebabai.tg2vk.domain.User;

import java.util.stream.Stream;

public interface UserService {
    Stream<User> findAllStarted();
}
