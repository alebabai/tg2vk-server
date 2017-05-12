package com.github.alebabai.tg2vk;

import com.github.alebabai.tg2vk.security.config.JwtSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@SpringBootApplication(scanBasePackages = {"com.github.alebabai.tg2vk"})
@EnableSwagger2
@Import(SpringDataRestConfiguration.class)
@EnableJpaRepositories(basePackages = "com.github.alebabai.tg2vk.repository")
public class Tg2VkApplication {

    public static void main(String[] args) {
        SpringApplication.run(Tg2VkApplication.class, args);
    }

    @Bean
    public Docket api(@Autowired JwtSettings settings) {
        final ApiKey apiKey = new ApiKey(settings.getHeaderName(), settings.getHeaderName(), "header");
        return new Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(Collections.singletonList(apiKey))
                .select()
                .paths(PathSelectors.ant("/api/users/**"))
                .build();
    }

    @Bean
    public MessageSourceAccessor messages(@Autowired MessageSource messageSource) {
        return new MessageSourceAccessor(messageSource);
    }
}
