package com.github.alebabai.tg2vk.frontend.controller;

import com.github.alebabai.tg2vk.service.PathResolverService;
import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.github.alebabai.tg2vk.util.constants.VkConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class AuthorizationController {

    @Autowired
    private VkService vkService;

    @Autowired
    private PathResolverService pathResolver;

    @GetMapping(PathConstants.ROOT_PATH)
    public String page(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", "Hello");
        return "page";
    }

    @RequestMapping(PathConstants.LOGIN_PATH)
    public String login(@RequestParam(name = "full_access", required = false, defaultValue = "false") boolean fullAccess) {
        String redirectUrl = pathResolver.getServerUrl() + PathConstants.AUTHORIZE_PATH;
        List<String> scopes = Arrays.asList(
                VkConstants.VK_SCOPE_AUDIO,
                VkConstants.VK_SCOPE_PHOTOS,
                VkConstants.VK_SCOPE_GROUPS,
                VkConstants.VK_SCOPE_STATUS,
                VkConstants.VK_SCOPE_NOTIFICATIONS,
                VkConstants.VK_SCOPE_FRIENDS,
                VkConstants.VK_SCOPE_DOCS,
                VkConstants.VK_SCOPE_OFFLINE
        );
        if (fullAccess) {
            redirectUrl = VkConstants.VK_URL_REDIRECT;
            scopes.add(VkConstants.VK_SCOPE_MESSAGES);
            scopes.add(VkConstants.VK_SCOPE_WALL);
        }
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + vkService.getAuthorizeUrl(redirectUrl, scopes.toArray(new String[]{}));
    }

    @RequestMapping(PathConstants.AUTHORIZE_PATH)
    public String authorize(@RequestParam String code) {
        vkService.authorize(code);
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + PathConstants.ROOT_PATH;
    }
}
