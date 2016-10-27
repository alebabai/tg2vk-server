package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.*;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
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

    private static final int MY_CHAT_ID = 129475042; //TODO remove hardcoded chat_id

    private final TelegramService tgService;
    private final VkService vkService;
    private final PathResolverService pathResolver;
    private final TemplateRendererService templateRenderer;

    @Autowired
    private LinkerServiceImpl(VkService vkService,
                              TelegramService tgService,
                              PathResolverService pathResolver,
                              TemplateRendererService templateRenderer) {
        this.vkService = vkService;
        this.tgService = tgService;
        this.pathResolver = pathResolver;
        this.templateRenderer = templateRenderer;
    }

    @PostConstruct
    @Override
    public void start() {
        tgService.fetchUpdates(update -> {
            final Message message = update.message();
            if (message != null) {
                String msgText = "Any message";
                SendMessage sendMessage = new SendMessage(update.message().chat().id(), msgText);
                if ("/login".equals(message.text())) {
                    sendMessage.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                            new InlineKeyboardButton("Login").url(pathResolver.getServerUrl() + PathConstants.LOGIN_PATH)
                    }));
                }
                tgService.send(sendMessage);
            }
        });

        vkService.fetchMessages((user, message) -> {
            try {
                Map<String, Object> context = new HashMap<>();
                context.put("user", String.format("%s %s", user.getFirstName(), user.getLastName()));
                context.put("body", message.getBody());
                final SendMessage sendMessage = new SendMessage(MY_CHAT_ID, templateRenderer.render("telegram/message.md", context))
                        .parseMode(ParseMode.Markdown);
                tgService.send(sendMessage);
            } catch (Exception e) {
                throw new RuntimeException("Error during vk message fetching", e);
            }
        });
    }
}
