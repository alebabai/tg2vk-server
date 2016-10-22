package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.LinkerService;
import com.github.alebabai.tg2vk.service.PathResolverService;
import com.github.alebabai.tg2vk.service.TelegramService;
import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class LinkerServiceImpl implements LinkerService {

    @Autowired
    private TelegramService tgService;

    @Autowired
    private VkService vkService;

    @Autowired
    private PathResolverService pathResolver;

    @PostConstruct
    @Override
    public void start() {
        tgService.fetchUpdates(update -> {
            SendMessage sendMessage = new SendMessage(update.message().chat().id(), "Test login");
            sendMessage.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                new InlineKeyboardButton("Login").url(pathResolver.getServerUrl() + PathConstants.LOGIN_PATH)
            }));
            tgService.send(sendMessage);
            return update;
        });
    }
}
