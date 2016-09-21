package com.github.alebabai.tg2vk.frontend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aliaksandr_Babai on 21.9.2016.
 */
@RestController
public class SomeController {
    @RequestMapping("/")
    public ModelAndView some(){
        return new ModelAndView("page", "title", "Sample page");
    }

    @GetMapping("rest")
    public Map<String, String> rest(){
        Map<String, String> result = new HashMap<>();
        result.put("greetings", "Hello");
        return result;
    }
}
