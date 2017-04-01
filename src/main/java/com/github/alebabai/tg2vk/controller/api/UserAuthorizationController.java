package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.service.UserService;
import com.github.alebabai.tg2vk.service.VkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestController
@RequestMapping("/users/authorize")
public class UserAuthorizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthorizationController.class);

    private static final String MSG_AUTH_SUCCESS = "Successfully authorized!";
    private static final String MSG_AUTH_CODE_ERROR = "Wrong vk authorization code!";
    private static final String MSG_AUTH_ID_TOKEN_ERROR = "Wrong userId or token!";

    private final VkService vkService;
    private final UserService userService;

    @Autowired
    public UserAuthorizationController(VkService vkService, UserService userService) {
        this.vkService = vkService;
        this.userService = userService;
    }

    @PostMapping(value = "/code")
    public ResponseEntity<String> authorize(@RequestParam String code, Authentication auth) {
        return vkService.authorize(code)
                .map(actor -> processAuthorization((Integer) auth.getPrincipal(), actor.getId(), actor.getAccessToken()))
                .orElse(ResponseEntity.badRequest().body(MSG_AUTH_CODE_ERROR));
    }

    @PostMapping("/implicit")
    public ResponseEntity<String> authorize(@RequestParam Integer vkId, @RequestParam String vkToken, Authentication auth) {
        return vkService.authorize(vkId, vkToken)
                .map(actor -> processAuthorization((Integer) auth.getPrincipal(), actor.getId(), actor.getAccessToken()))
                .orElse(ResponseEntity.badRequest().body(MSG_AUTH_ID_TOKEN_ERROR));
    }

    private ResponseEntity<String> processAuthorization(Integer tgId, Integer vkId, String vkToken) {
        final User user = userService.createOrUpdate(tgId, vkId, vkToken);
        LOGGER.debug("User successfully created {}", user);
        return ResponseEntity.ok(MSG_AUTH_SUCCESS);
    }
}
