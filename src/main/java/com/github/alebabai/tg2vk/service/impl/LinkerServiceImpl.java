package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.*;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class LinkerServiceImpl implements LinkerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkerService.class);

    private final TelegramService tgService;
    private final VkService vkService;
    private final TemplateRendererService templateRenderer;

    @Autowired
    private LinkerServiceImpl(VkService vkService,
                              TelegramService tgService,
                              PathResolverService pathResolver,
                              TemplateRendererService templateRenderer) {
        this.vkService = vkService;
        this.tgService = tgService;
        this.templateRenderer = templateRenderer;
    }

    @PostConstruct
    @Override
    public void init() {
        tgService.fetchWebHookUpdates();

        vkService.fetchMessages((user, message) -> {
            try {
                Map<String, Object> context = new HashMap<>();
                context.put("user", String.format("%s %s", user.getFirstName(), user.getLastName()));
                context.put("body", message.getBody());
                final SendMessage sendMessage = new SendMessage(message.getUserId(), templateRenderer.render("telegram/message.md", context))
                        .parseMode(ParseMode.Markdown);
                tgService.send(sendMessage);
            } catch (Exception e) {
                LOGGER.error("Error during vk message fetching: ", e);
            }
        });
    }
}
