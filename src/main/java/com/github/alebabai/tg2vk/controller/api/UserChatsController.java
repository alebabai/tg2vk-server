package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.domain.Chat;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.repository.UserRestRepository;
import com.github.alebabai.tg2vk.service.vk.VkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RepositoryRestController
@RequestMapping("/users/{id}/chats")
@ExposesResourceFor(User.class)
public class UserChatsController {

    private final UserRestRepository userRepository;
    private final VkService vkService;
    private final EntityLinks entityLinks;

    @Autowired
    public UserChatsController(UserRestRepository userRepository, VkService vkService, EntityLinks entityLinks) {
        this.userRepository = userRepository;
        this.vkService = vkService;
        this.entityLinks = entityLinks;
    }

    @GetMapping("/vk")
    @ResponseBody
    public Resources<Chat> getVkChats(@PathVariable Integer id, @RequestParam(required = false) String query) {
        return Optional.ofNullable(userRepository.findOne(id))
                .map(user -> {
                    final Collection<Chat> chats = vkService.findChats(user, query);
                    return new Resources<>(chats, linkTo(methodOn(this.getClass()).getVkChats(id, query)).withSelfRel(), entityLinks.linkToSingleResource(user));
                })
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
