package com.github.alebabai.tg2vk.frontend.controller.page;

import com.github.alebabai.tg2vk.util.constants.PathConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.Map;

@Controller
public class PageController {

    @GetMapping(PathConstants.ROOT)
    public String page(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", "Hello");
        return "page.html";
    }
}
