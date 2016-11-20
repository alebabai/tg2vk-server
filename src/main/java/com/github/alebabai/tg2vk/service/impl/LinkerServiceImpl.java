package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.service.LinkerService;
import com.github.alebabai.tg2vk.service.TelegramService;
import com.github.alebabai.tg2vk.service.TemplateRendererService;
import com.github.alebabai.tg2vk.service.VkService;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.vk.api.sdk.client.actors.UserActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@SessionScope(proxyMode = ScopedProxyMode.INTERFACES)
public class LinkerServiceImpl implements LinkerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkerService.class);

    private final TelegramService tgService;
    private final VkService vkService;
    private final TemplateRendererService templateRenderer;

    private AtomicBoolean isDaemonActive;

    @Autowired
    private LinkerServiceImpl(VkService vkService,
                              TelegramService tgService,
                              TemplateRendererService templateRenderer) {
        this.vkService = vkService;
        this.tgService = tgService;
        this.templateRenderer = templateRenderer;
    }

    @Override
    public void start(User user) {
        LOGGER.debug("Start messages linking for {}", user);
        final UserActor actor = new UserActor(user.getVkId(), user.getVkToken());
        this.isDaemonActive = vkService.fetchMessages(actor, (profile, message) -> {
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
    }

    @PreDestroy
    @Override
    public void stop() {
        if (isDaemonActive != null) {
            isDaemonActive.lazySet(false);
            LOGGER.debug("Stop messages linking");
        }
    }
}
