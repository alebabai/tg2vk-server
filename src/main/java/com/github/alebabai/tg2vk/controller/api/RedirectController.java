package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.service.PathResolver;
import com.github.alebabai.tg2vk.service.VkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.github.alebabai.tg2vk.util.constants.VkConstants.*;

@RestController
@RequestMapping("/api/redirect")
public class RedirectController {

    private final VkService vkService;
    private final PathResolver pathResolver;

    @Autowired
    public RedirectController(VkService vkService,
                              PathResolver pathResolver) {
        this.vkService = vkService;
        this.pathResolver = pathResolver;
    }

    @GetMapping("/vk-login")
    public void vkLogin(HttpServletResponse response) throws IOException {
        final String[] scopes = {
                VK_SCOPE_MESSAGES,
                VK_SCOPE_OFFLINE
        };
        response.sendRedirect(vkService.getAuthorizeUrl(VK_URL_REDIRECT, scopes));
    }

    @GetMapping(value = {"/client", "/client/{relativePath}"})
    public void client(@PathVariable(required = false) String relativePath, @RequestParam String token, HttpServletResponse response) throws IOException {
        final String redirectUri = UriComponentsBuilder
                .fromUriString(pathResolver.resolveClientUrl("sign-in"))
                .queryParam("token", token)
                .queryParam("redirect", "/" + relativePath)
                .toUriString();
        response.sendRedirect(redirectUri);
    }
}
