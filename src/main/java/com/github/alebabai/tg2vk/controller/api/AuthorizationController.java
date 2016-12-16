package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.service.UserService;
import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.vk.api.sdk.client.actors.UserActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import static com.github.alebabai.tg2vk.util.constants.VkConstants.*;

@RestController(PathConstants.API_AUTH)
public class AuthorizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationController.class);

    private static final String MSG_AUTH_SUCCESS = "Successfully authorized!";
    private static final String MSG_AUTH_CODE_ERROR = "Wrong vk authorization code!";
    private static final String MSG_AUTH_ID_TOKEN_ERROR = "Wrong userId or token!";

    private final VkService vkService;
    private final UserService userService;

    @Autowired
    public AuthorizationController(VkService vkService,
                                   UserService userService) {
        this.vkService = vkService;
        this.userService = userService;
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
    public ResponseEntity<String> authorize(@RequestParam Integer tgId, @RequestParam String code) {
        return vkService.authorize(code)
                .map(actor -> processAuthorization(tgId, actor))
                .orElse(ResponseEntity.badRequest().body(MSG_AUTH_CODE_ERROR));
    }

    @PostMapping(PathConstants.API_AUTH_AUTHORIZE_IMPLICIT)
    public ResponseEntity<String> authorize(@RequestParam Integer tgId, @RequestParam Integer vkId, @RequestParam String vkToken) {
        return vkService.authorize(vkId, vkToken)
                .map(actor -> processAuthorization(tgId, actor))
                .orElse(ResponseEntity.badRequest().body(MSG_AUTH_ID_TOKEN_ERROR));
    }

    private ResponseEntity<String> processAuthorization(@RequestParam Integer tgId, UserActor actor) {
        final User user = userService.createOrUpdate(tgId, actor.getId(), actor.getAccessToken());
        LOGGER.debug("User successfully created {}", user);
        return ResponseEntity.ok(MSG_AUTH_SUCCESS);
    }
}
