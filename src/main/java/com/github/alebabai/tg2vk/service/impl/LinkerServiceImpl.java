package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.LinkerService;
import com.github.alebabai.tg2vk.service.TelegramService;
import com.github.alebabai.tg2vk.service.VkService;
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

    @PostConstruct
    @Override
    public void start() {
        tgService.fetchUpdates(update -> {
            SendMessage sendMessage = new SendMessage(update.message().chat().id(), "Test login");
            sendMessage.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                new InlineKeyboardButton("Login").url(vkService.getAuthorizeUrl())
            }));
            tgService.send(sendMessage);
            return update;
        });
    }
}
