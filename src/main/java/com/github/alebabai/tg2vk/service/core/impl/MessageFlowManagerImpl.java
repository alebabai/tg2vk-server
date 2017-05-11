package com.github.alebabai.tg2vk.service.core.impl;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.core.MessageFlowManager;
import com.github.alebabai.tg2vk.service.vk.VkService;
import com.vk.api.sdk.objects.messages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

@Service
public class MessageFlowManagerImpl implements MessageFlowManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageFlowManagerImpl.class);
    private final UserRepository userRepository;
    private final VkService vkService;
    private final LinkerServiceImpl linkerService;
    private final ExecutorService executorService;
    private final ConcurrentMap<Integer, Future<Integer>> taskPool;
    @Value("${tg2vk.vk.service.processor.auto_init_pool:false}")
    private boolean autoInitPool;

    @Autowired
    public MessageFlowManagerImpl(UserRepository userRepository,
                                  VkService vkService,
                                  LinkerServiceImpl linkerService) {
        this.userRepository = userRepository;
        this.vkService = vkService;
        this.linkerService = linkerService;
        this.executorService = Executors.newCachedThreadPool();
        this.taskPool = new ConcurrentHashMap<>();
    }

    @PostConstruct
    protected void init() {
        if (autoInitPool) {
            userRepository.findAllStarted().forEach(this::start);
        }
    }

    @Transactional
    @Override
    public void start(User user) {
        final boolean isStarted = taskPool.keySet()
                .stream()
                .anyMatch(id -> Objects.equals(id, user.getId()));
        if (!isStarted) {
            final BiConsumer<com.vk.api.sdk.objects.users.User, Message> messageHandler = linkerService.getVkMessageHandler(user.getId());
            final Future<Integer> task = executorService.submit(() -> vkService.fetchMessages(user, messageHandler));
            taskPool.put(user.getId(), task);
            user.getSettings().setStarted(true);
            userRepository.save(user);
            LOGGER.debug("Start vk messages processing for {}", user);
        }
    }

    @Transactional
    @Override
    public void stop(User user) {
        user.getSettings().setStarted(false);
        userRepository.save(user);
        Optional.ofNullable(taskPool.get(user.getId()))
                .ifPresent(task -> {
                    task.cancel(true);
                    user.getSettings().setStarted(false);
                    userRepository.save(user);
                    taskPool.remove(user.getId());
                    LOGGER.debug("Stop messages processing for {}", user);
                });
    }
}
