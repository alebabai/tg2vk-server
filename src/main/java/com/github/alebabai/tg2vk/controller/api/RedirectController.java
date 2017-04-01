package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.service.PathResolver;
import com.github.alebabai.tg2vk.service.VkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(value = "/client")
    public void client(@RequestParam String token, HttpServletResponse response) throws IOException {
        final String clientRedirectUrl = String.format("%s?token=%s", pathResolver.getClientUrl(), token);
        response.sendRedirect(clientRedirectUrl);
    }
}
