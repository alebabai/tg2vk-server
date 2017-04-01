package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.domain.Chat;
import com.github.alebabai.tg2vk.service.TelegramService;
import com.github.alebabai.tg2vk.service.VkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.Collections;

@RepositoryRestController
@RequestMapping("/users/{id}/chats")
public class UserChatsController {

    private final VkService vkService;
    private final TelegramService telegramService;

    @Autowired
    public UserChatsController(VkService vkService, TelegramService telegramService) {
        this.vkService = vkService;
        this.telegramService = telegramService;
    }

    @GetMapping("/vk")
    public Collection<Chat> getVkChats(@PathVariable Integer id) {
        return Collections.emptyList();
    }

    @GetMapping("/tg")
    public Collection<Chat> getTgChats(@PathVariable Integer id) {
        return Collections.emptyList();
    }
}
