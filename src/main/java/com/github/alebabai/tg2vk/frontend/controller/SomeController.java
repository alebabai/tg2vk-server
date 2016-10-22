package com.github.alebabai.tg2vk.frontend.controller;

import com.github.alebabai.tg2vk.service.VkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Map;

@Controller
public class SomeController {
    @Autowired
    private VkService vkService;

    @GetMapping("/")
    public String page(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", "Hello");
        return "page";
    }

    @RequestMapping("/login")
    public void login(@RequestParam String code) {
        vkService.authorize(code);
    }
}
