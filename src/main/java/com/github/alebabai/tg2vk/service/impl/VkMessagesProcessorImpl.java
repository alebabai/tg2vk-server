package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.service.UserService;
import com.github.alebabai.tg2vk.service.VkMessagesProcessor;
import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.constants.EnvConstants;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Service
public class VkMessagesProcessorImpl implements VkMessagesProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(VkMessagesProcessorImpl.class);

    private final UserService userService;
    private final VkService vkService;
    private final LinkerServiceImpl linkerService;
    private final Environment env;
    private Map<Integer, CompletableFuture<Integer>> taskPool;

    @Autowired
    public VkMessagesProcessorImpl(UserService userService,
                                   VkService vkService,
                                   LinkerServiceImpl linkerService,
                                   Environment env) {
        this.userService = userService;
        this.vkService = vkService;
        this.linkerService = linkerService;
        this.env = env;
        this.taskPool = new HashMap<>();
    }

    @Async
    @PostConstruct
    protected void init() {
        final boolean autoInit = env.getProperty(EnvConstants.PROP_VK_AUTO_INIT_POOL, Boolean.TYPE, true);
        if (autoInit) {
            userService.findAllStarted().forEach(this::start);
        }
    }

    @Override
    @Transactional
    public void start(User user) {
        final boolean isStarted = taskPool.keySet()
                .stream()
                .anyMatch(id -> Objects.equals(id, user.getId()));
        if (!isStarted) {
            final UserActor actor = new UserActor(user.getVkId(), user.getVkToken());
            final BiConsumer<com.vk.api.sdk.objects.users.User, Message> messageHandler = linkerService.getVkMessageHandler(user);
            final CompletableFuture<Integer> task = vkService.fetchMessages(actor, messageHandler);
            taskPool.put(user.getId(), task);
            user.getSettings().setStarted(true);
            userService.updateUserSettings(user.getSettings());
            LOGGER.debug("Start vk messages processing for {}", user);
        }
    }

    @Override
    @Transactional
    public void stop(User user) {
        Optional.ofNullable(taskPool.get(user.getId()))
                .ifPresent(task -> {
                    task.cancel(true);
                    user.getSettings().setStarted(false);
                    userService.updateUserSettings(user.getSettings());
                    taskPool.remove(user.getId());
                    LOGGER.debug("Stop messages processing for {}", user);
                });
    }
}
