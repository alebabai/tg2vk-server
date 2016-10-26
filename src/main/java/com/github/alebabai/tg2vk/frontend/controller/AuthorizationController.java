package com.github.alebabai.tg2vk.frontend.controller;

import com.github.alebabai.tg2vk.service.PathResolverService;
import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.alebabai.tg2vk.util.constants.VkConstants.*;

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
        List<String> scopes = Stream.of(
                VK_SCOPE_AUDIO,
                VK_SCOPE_PHOTOS,
                VK_SCOPE_GROUPS,
                VK_SCOPE_STATUS,
                VK_SCOPE_NOTIFICATIONS,
                VK_SCOPE_FRIENDS,
                VK_SCOPE_DOCS,
                VK_SCOPE_OFFLINE
        ).collect(Collectors.toList());
        if (fullAccess) {
            redirectUrl = VK_URL_REDIRECT;
            scopes.add(VK_SCOPE_MESSAGES);
            scopes.add(VK_SCOPE_WALL);
        }
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + vkService.getAuthorizeUrl(redirectUrl, scopes.toArray(new String[]{}));
    }

    @RequestMapping(PathConstants.AUTHORIZE_PATH)
    public String authorize(@RequestParam String code) {
        vkService.authorize(code);
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + PathConstants.ROOT_PATH;
    }
}
