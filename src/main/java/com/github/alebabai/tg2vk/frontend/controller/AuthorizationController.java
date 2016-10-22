package com.github.alebabai.tg2vk.frontend.controller;

import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.util.Date;
import java.util.Map;

@Controller
public class AuthorizationController {
    @Autowired
    private VkService vkService;

    @GetMapping(PathConstants.ROOT_PATH)
    public String page(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", "Hello");
        return "page";
    }

    @RequestMapping(PathConstants.LOGIN_PATH)
    public String login(Device device) {

        String result = PathConstants.ERROR_PATH;
        switch (device.getDevicePlatform()) {
            case IOS:
            case ANDROID:
                result = UrlBasedViewResolver.REDIRECT_URL_PREFIX + vkService.getAuthorizeUrl(true);
                break;
            case UNKNOWN:
                result = UrlBasedViewResolver.REDIRECT_URL_PREFIX + vkService.getAuthorizeUrl(false);
                break;
            default:
                break;
        }
        return result;
    }

    @RequestMapping(PathConstants.AUTHORIZE_PATH)
    public String authorize(@RequestParam String code) {
        vkService.authorize(code);
        return UrlBasedViewResolver.REDIRECT_URL_PREFIX + PathConstants.ROOT_PATH;
    }
}
