package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.domain.Chat;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.TelegramService;
import com.github.alebabai.tg2vk.service.VkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Optional;

@RepositoryRestController
@RequestMapping("/users/{id}/chats")
public class UserChatsController {

    private final UserRepository userRepository;
    private final VkService vkService;
    private final TelegramService telegramService;

    @Autowired
    public UserChatsController(UserRepository userRepository, VkService vkService, TelegramService telegramService) {
        this.userRepository = userRepository;
        this.vkService = vkService;
        this.telegramService = telegramService;
    }

    @GetMapping("/vk")
    @ResponseBody
    public Collection<Chat> getVkChats(@PathVariable Integer id) {
        return Optional.ofNullable(userRepository.findOne(id))
                .map(vkService::getChats)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @GetMapping("/tg")
    @ResponseBody
    public Collection<Chat> getTgChats(@PathVariable Integer id) {
        return Optional.ofNullable(userRepository.findOne(id))
                .map(telegramService::getChats)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}