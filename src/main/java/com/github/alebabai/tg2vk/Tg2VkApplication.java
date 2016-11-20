package com.github.alebabai.tg2vk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.github.alebabai.tg2vk"})
@EnableJpaRepositories(basePackages = "com.github.alebabai.tg2vk.repository")
@PropertySource("classpath:application.properties")
public class Tg2VkApplication {

    public static void main(String[] args) {
        SpringApplication.run(Tg2VkApplication.class, args);
    }
}
