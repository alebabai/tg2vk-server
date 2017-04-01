package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.service.PathResolver;
import com.github.alebabai.tg2vk.service.UserService;
import com.github.alebabai.tg2vk.service.VkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.URISyntaxException;

@RepositoryRestController
@RequestMapping("api/users/authorize")
public class UserAuthorizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthorizationController.class);

    private final VkService vkService;
    private final UserService userService;
    private final PathResolver pathResolver;

    @Autowired
    public UserAuthorizationController(VkService vkService, UserService userService, PathResolver pathResolver) {
        this.vkService = vkService;
        this.userService = userService;
        this.pathResolver = pathResolver;
    }

    @PostMapping(value = "/code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource<User>> authorize(@RequestParam String code, Authentication auth) {
        return vkService.authorize(code)
                .map(actor -> processAuthorization((Integer) auth.getPrincipal(), actor.getId(), actor.getAccessToken()))
                .orElseThrow(() -> new IllegalArgumentException("Wrong vk authorization code!"));
    }

    @PostMapping(value = "/implicit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resource<User>> authorize(@RequestParam Integer vkId, @RequestParam String vkToken, Authentication auth) {
        return vkService.authorize(vkId, vkToken)
                .map(actor -> processAuthorization((Integer) auth.getPrincipal(), actor.getId(), actor.getAccessToken()))
                .orElseThrow(() -> new IllegalArgumentException("Wrong userId or token!"));
    }

    private ResponseEntity<Resource<User>> processAuthorization(Integer tgId, Integer vkId, String vkToken) {
        try {
            final User user = userService.createOrUpdate(tgId, vkId, vkToken);
            final String href = pathResolver.getAbsoluteUrl(String.format("/api/users/%d", user.getId()));
            final Resource<User> resource = new Resource<>(user, new Link(href, "self"));
            LOGGER.debug("User successfully created {}", user);
            return ResponseEntity
                    .created(new URI(href))
                    .body(resource);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Can't resolve user URI", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error happened during user authorization", e);
        }
    }
}
