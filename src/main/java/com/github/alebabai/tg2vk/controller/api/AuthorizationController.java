package com.github.alebabai.tg2vk.controller.api;

import com.github.alebabai.tg2vk.service.PathResolverService;
import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import static com.github.alebabai.tg2vk.util.constants.VkConstants.VK_SCOPE_MESSAGES;
import static com.github.alebabai.tg2vk.util.constants.VkConstants.VK_SCOPE_OFFLINE;
import static com.github.alebabai.tg2vk.util.constants.VkConstants.VK_URL_REDIRECT;

@Controller(PathConstants.API_AUTHORIZATION)
public class AuthorizationController {

    @Autowired
    private VkService vkService;

    @Autowired
    private PathResolverService pathResolver;

    @GetMapping(PathConstants.API_LOGIN)
    public String login() {
        final String[] scopes = {
                VK_SCOPE_MESSAGES,
                VK_SCOPE_OFFLINE
        };
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + vkService.getAuthorizeUrl(VK_URL_REDIRECT, scopes);
    }

    /**
     * An alternative method to support authorization from other clients;
     * @param code vk authorization code
     * @return redirect path;
     */
    @RequestMapping(PathConstants.API_AUTHORIZE)
    public String authorize(@RequestParam String code) {
        vkService.authorize(code);
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + PathConstants.ROOT;
    }
}
