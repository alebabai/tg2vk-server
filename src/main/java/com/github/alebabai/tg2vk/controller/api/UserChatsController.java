package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.domain.Chat;
import com.github.alebabai.tg2vk.repository.UserRestRepository;
import com.github.alebabai.tg2vk.service.VkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RepositoryRestController
@RequestMapping("/users/{id}/chats")
public class UserChatsController {

    private final UserRestRepository userRepository;
    private final VkService vkService;

    @Autowired
    public UserChatsController(UserRestRepository userRepository, VkService vkService) {
        this.userRepository = userRepository;
        this.vkService = vkService;
    }

    @GetMapping("/vk")
    @ResponseBody
    public Collection<Chat> getVkChats(@PathVariable Integer id, @RequestParam(required = false) String query) {
        return Optional.ofNullable(userRepository.findOne(id))
                .map(user -> vkService.findChats(user, query))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
