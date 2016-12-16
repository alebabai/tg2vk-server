package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Stream<User> findAllStarted() {
        return userRepository.findAllStarted();
    }
}
