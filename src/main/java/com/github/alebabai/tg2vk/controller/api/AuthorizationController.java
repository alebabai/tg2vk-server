package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.security.service.JwtTokenFactoryService;
import com.github.alebabai.tg2vk.service.UserService;
import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.github.alebabai.tg2vk.util.constants.VkConstants.*;

@RestController(PathConstants.API_AUTH)
public class AuthorizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationController.class);

    private static final String MSG_AUTH_SUCCESS = "Successfully authorized!";
    private static final String MSG_AUTH_CODE_ERROR = "Wrong vk authorization code!";
    private static final String MSG_AUTH_ID_TOKEN_ERROR = "Wrong userId or token!";

    private final VkService vkService;
    private final UserService userService;
    private final JwtTokenFactoryService tokenFactory;

    @Autowired
    public AuthorizationController(VkService vkService,
                                   UserService userService,
                                   JwtTokenFactoryService tokenFactory) {
        this.vkService = vkService;
        this.userService = userService;
        this.tokenFactory = tokenFactory;
    }

    @GetMapping(PathConstants.API_AUTH_LOGIN)
    public void login(HttpServletResponse response) throws IOException {
        final String[] scopes = {
                VK_SCOPE_MESSAGES,
                VK_SCOPE_OFFLINE
        };
        response.sendRedirect(vkService.getAuthorizeUrl(VK_URL_REDIRECT, scopes));
    }

    @PostMapping(PathConstants.API_AUTH_AUTHORIZE_CODE)
    public ResponseEntity<String> authorize(@RequestParam String code, Authentication auth) {
        return vkService.authorize(code)
                .map(actor -> processAuthorization((Integer) auth.getPrincipal(), actor.getId(), actor.getAccessToken()))
                .orElse(ResponseEntity.badRequest().body(MSG_AUTH_CODE_ERROR));
    }

    @PostMapping(PathConstants.API_AUTH_AUTHORIZE_IMPLICIT)
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
