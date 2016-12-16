package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.vk.api.sdk.client.actors.UserActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.util.Optional;

import static com.github.alebabai.tg2vk.util.constants.VkConstants.*;

@RestController(PathConstants.API_AUTH)
public class AuthorizationController {

    private static final String MSG_AUTH_SUCCESS = "Successfully authorized!";
    private static final String MSG_AUTH_CODE_ERROR = "Wrong vk authorization code!";
    private static final String MSG_AUTH_ID_TOKEN_ERROR = "Wrong userId or token!";

    private final VkService vkService;

    @Autowired
    public AuthorizationController(VkService vkService) {
        this.vkService = vkService;
    }

    @GetMapping(PathConstants.API_AUTH_LOGIN)
    public String login() {
        final String[] scopes = {
                VK_SCOPE_MESSAGES,
                VK_SCOPE_OFFLINE
        };
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + vkService.getAuthorizeUrl(VK_URL_REDIRECT, scopes);
    }

    @PostMapping(PathConstants.API_AUTH_AUTHORIZE_CODE)
    public ResponseEntity<String> authorize(@RequestParam String code) {
        final Optional<UserActor> actor = vkService.authorize(code);
        if (actor.isPresent()) {
            return ResponseEntity.ok(MSG_AUTH_SUCCESS);
        }
        return ResponseEntity.badRequest().body(MSG_AUTH_CODE_ERROR);
    }

    @PostMapping(PathConstants.API_AUTH_AUTHORIZE_IMPLICIT)
    public ResponseEntity<String> authorize(@RequestParam Integer userId, @RequestParam String token) {
        final Optional<UserActor> actor = vkService.authorize(userId, token);
        if (actor.isPresent()) {
            return ResponseEntity.ok(MSG_AUTH_SUCCESS);
        }
        return ResponseEntity.badRequest().body(MSG_AUTH_ID_TOKEN_ERROR);
    }
}
