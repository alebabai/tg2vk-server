package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.dto.CodeAuthPayload;
import com.github.alebabai.tg2vk.dto.ImplicitAuthPayload;
import com.github.alebabai.tg2vk.service.core.MessageFlowManager;
import com.github.alebabai.tg2vk.service.core.UserService;
import com.github.alebabai.tg2vk.service.vk.VkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RepositoryRestController
@RequestMapping("/users/authorize")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
@ExposesResourceFor(User.class)
public class UserAuthorizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthorizationController.class);

    private final UserService userService;
    private final VkService vkService;
    private final MessageFlowManager messageFlowManager;
    private final EntityLinks entityLinks;

    @Autowired
    public UserAuthorizationController(UserService userService, VkService vkService, MessageFlowManager messageFlowManager, EntityLinks entityLinks) {
        this.userService = userService;
        this.vkService = vkService;
        this.messageFlowManager = messageFlowManager;
        this.entityLinks = entityLinks;
    }

    @PostMapping(value = "/code")
    @ResponseBody
    @ResponseStatus(code = HttpStatus.CREATED)
    public Resource<User> authorize(@RequestBody CodeAuthPayload payload, Authentication auth) {
        return vkService.authorize(payload.code())
                .map(actor -> processAuthorization((Integer) auth.getPrincipal(), actor.getId(), actor.getAccessToken()))
                .orElseThrow(() -> new IllegalArgumentException("Wrong vk authorization code!"));
    }

    @PostMapping(value = "/implicit")
    @ResponseBody
    @ResponseStatus(code = HttpStatus.CREATED)
    public Resource<User> authorize(@RequestBody ImplicitAuthPayload payload, Authentication auth) {
        return vkService.authorize(payload.vkId(), payload.vkToken())
                .map(actor -> processAuthorization((Integer) auth.getPrincipal(), actor.getId(), actor.getAccessToken()))
                .orElseThrow(() -> new IllegalArgumentException("Wrong userId or token!"));
    }

    private Resource<User> processAuthorization(Integer tgId, Integer vkId, String vkToken) {
        try {
            final User user = userService.createOrUpdate(tgId, vkId, vkToken);
            LOGGER.debug("User successfully created {}", user);
            messageFlowManager.stop(user);
            return new Resource<>(user, entityLinks.linkToSingleResource(user));
        } catch (Exception e) {
            throw new IllegalStateException("Error happened during user authorization", e);
        }
    }
}
