package com.github.alebabai.tg2vk.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/")
public class PageController {

    @GetMapping
    public String page(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", "Hello");
        return "page.html";
    }
}
