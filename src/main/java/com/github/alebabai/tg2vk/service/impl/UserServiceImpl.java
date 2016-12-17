package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.exception.UserCreationException;
import com.github.alebabai.tg2vk.repository.ChatSettingsRepository;
import com.github.alebabai.tg2vk.repository.RoleRepository;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.UserService;
import com.github.alebabai.tg2vk.util.constants.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ChatSettingsRepository chatSettingsRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ChatSettingsRepository chatSettingsRepository,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.chatSettingsRepository = chatSettingsRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Stream<User> findAllStarted() {
        return userRepository.findAllStarted();
    }

    @Transactional
    @Override
    public Optional<User> findOneByVkId(Integer id) {
        return userRepository.findOneByVkId(id);
    }

    @Transactional
    @Override
    public Optional<User> findOneByTgId(Integer id) {
        return userRepository.findOneByVkId(id);
    }


    @Transactional
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User createOrUpdate(Integer tgId, Integer newVkId, String newVkToken) throws UserCreationException {
        Assert.notNull(tgId, "tgId is required param!");
        Assert.notNull(tgId, "vkId is required param!");
        Assert.notNull(tgId, "vkToken is required param!");
        return userRepository.findOneByTgId(tgId)
                .map(user -> {
                    chatSettingsRepository.delete(user.getChatsSettings());
                    user.setVkId(newVkId).setVkToken(newVkToken);
                    return userRepository.save(user);
                })
                .orElseGet(() -> create(tgId, newVkId, newVkToken));
    }

    @Transactional
    @Override
    public User create(Integer tgId, Integer vkId, String vkToken) throws UserCreationException {
        Assert.notNull(tgId, "tgId is required param!");
        Assert.notNull(tgId, "vkId is required param!");
        Assert.notNull(tgId, "vkToken is required param!");
        return roleRepository.findOneByName(SecurityConstants.ROLE_USER)
                .map(role -> userRepository.save(new User()
                        .setVkId(vkId)
                        .setTgId(tgId)
                        .setVkToken(vkToken)
                        .setRoles(Collections.singleton(role))))
                .orElseThrow(() -> new UserCreationException("Required role not found in the database"));
    }
}
