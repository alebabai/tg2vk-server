package com.github.alebabai.tg2vk.config;

import com.github.alebabai.tg2vk.util.constants.Constants;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = {"com.github.alebabai.tg2vk"})
public class Tg2VkConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public TelegramBot bot() {
       return TelegramBotAdapter.build(environment.getRequiredProperty(Constants.PROP_TELEGRAM_BOT_TOKEN));
    }

    @Bean
    public VkApiClient vkApi() {
        return new VkApiClient(new HttpTransportClient());
    }
}
