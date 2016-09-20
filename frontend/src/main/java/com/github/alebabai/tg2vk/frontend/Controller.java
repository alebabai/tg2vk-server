package com.github.alebabai.tg2vk.frontend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Aliaksandr_Babai on 20.9.2016.
 */
@RestController
public class Controller {

    @RequestMapping("/")
    public Pojo root() {
        return new Pojo("text");
    }

    @RequestMapping("tpl")
    public ModelAndView tpl(){
        return new ModelAndView("page", "title", "Template page");
    }

    class Pojo {
        private String title;

        public Pojo(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public Pojo setTitle(String title) {
            this.title = title;
            return this;
        }
    }


}
