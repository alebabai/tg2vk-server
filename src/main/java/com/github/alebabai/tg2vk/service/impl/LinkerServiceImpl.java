package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.service.*;
import com.github.alebabai.tg2vk.util.constants.EnvConstants;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.vk.api.sdk.client.actors.UserActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class LinkerServiceImpl implements LinkerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkerServiceImpl.class);

    private final Environment env;
    private final UserService userService;
    private final TelegramService tgService;
    private final VkService vkService;
    private final TemplateRendererService templateRenderer;
    private final Map<Integer, AtomicBoolean> daemonStates;

    @Autowired
    public LinkerServiceImpl(Environment env,
                             UserService userService,
                             VkService vkService,
                             TelegramService tgService,
                             TemplateRendererService templateRenderer) {
        this.env = env;
        this.userService = userService;
        this.vkService = vkService;
        this.tgService = tgService;
        this.templateRenderer = templateRenderer;
        this.daemonStates = new HashMap<>();
    }

    @PostConstruct
    protected void init() {
        if (env.getProperty(EnvConstants.PROP_VK_AUTO_INIT_POOL, Boolean.TYPE, true)) {
            userService.findAllStarted().forEach(this::start);
        }
    }

    @Transactional
    @Override
    public void start(User user) {
        LOGGER.debug("Start messages linking for {}", user);
        final UserActor actor = new UserActor(user.getVkId(), user.getVkToken());
        final AtomicBoolean isDaemonActive = vkService.fetchMessages(actor, (profile, message) -> {
            try {
                Map<String, Object> context = new HashMap<>();
                context.put("user", String.format("%s %s", profile.getFirstName(), profile.getLastName()));
                context.put("body", message.getBody());
                final SendMessage sendMessage = new SendMessage(user.getTgId(), templateRenderer.render("telegram/message.md", context))
                        .parseMode(ParseMode.Markdown);
                tgService.send(sendMessage);
            } catch (Exception e) {
                LOGGER.error("Error during vk message fetching: ", e);
            }
        });
        daemonStates.put(user.getId(), isDaemonActive);
        user.getSettings().started(isDaemonActive.get());
        userService.updateUserSettings(user.getSettings());
    }

    @Transactional
    @Override
    public void stop(User user) {
        final AtomicBoolean isDaemonActive = daemonStates.get(user.getId());
        if (isDaemonActive != null) {
            final boolean state = false;
            isDaemonActive.lazySet(state);
            daemonStates.remove(user.getId());
            user.getSettings().started(state);
            userService.updateUserSettings(user.getSettings());
            LOGGER.debug("Messages linking messages for {} has been stopped", user);
        }
    }
}
