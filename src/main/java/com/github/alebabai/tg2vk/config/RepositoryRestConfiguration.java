package com.github.alebabai.tg2vk.config;

import com.github.alebabai.tg2vk.security.config.JwtSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Configuration
public class RepositoryRestConfiguration extends RepositoryRestConfigurerAdapter {

    @Autowired
    @Qualifier("jsr303Validator")
    private Validator validator;

    @Autowired
    private JwtSettings settings;

    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        validatingListener.addValidator("beforeCreate", validator);
        validatingListener.addValidator("beforeSave", validator);
    }

    @Override
    public void configureRepositoryRestConfiguration(org.springframework.data.rest.core.config.RepositoryRestConfiguration config) {
        config.getCorsRegistry()
                .addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins("*")
                .exposedHeaders(settings.getHeaderName());
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .exposedHeaders(settings.getHeaderName())
                        .allowedOrigins("*")
                        .allowedMethods("*");
            }
        };
    }
}
