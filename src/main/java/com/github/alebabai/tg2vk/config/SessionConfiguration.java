package com.github.alebabai.tg2vk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@EnableSpringHttpSession
public class SessionConfiguration {

    @Bean
    public MapSessionRepository sessionRepository() {
        return new MapSessionRepository();
    }
}
