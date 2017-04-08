package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.domain.Role;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.domain.UserSettings;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public User createOrUpdate(Integer tgId, Integer vkId, String vkToken) {
        Assert.notNull(tgId, "tgId is required param!");
        Assert.notNull(tgId, "vkId is required param!");
        Assert.notNull(tgId, "vkToken is required param!");
        return userRepository.findOneByTgId(tgId)
                .map(user -> {
                    user.setChatsSettings(Collections.emptySet());
                    user.setVkId(vkId).setVkToken(vkToken);
                    return userRepository.save(user);
                })
                .orElseGet(() -> userRepository.save(new User(tgId, vkId, vkToken, new UserSettings())
                        .setRoles(Collections.singleton(Role.USER))));
    }
}
