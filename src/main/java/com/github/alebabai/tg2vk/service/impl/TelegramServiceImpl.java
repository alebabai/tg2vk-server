package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.PathResolverService;
import com.github.alebabai.tg2vk.service.TelegramService;
import com.github.alebabai.tg2vk.util.constants.Constants;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.function.Consumer;

@Service
public class TelegramServiceImpl implements TelegramService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramService.class);

    private final TelegramBot bot;
    private final ResourceLoader resourceLoader;
    private final PathResolverService pathResolver;

    @Autowired
    private TelegramServiceImpl(Environment environment, ResourceLoader resourceLoader, PathResolverService pathResolver) {
        this.bot = TelegramBotAdapter.build(environment.getRequiredProperty(Constants.PROP_TELEGRAM_BOT_TOKEN));
        this.resourceLoader = resourceLoader;
        this.pathResolver = pathResolver;
    }

    @Override
    public void fetchLongPollingUpdates(Consumer<? super Update> callback) {
        bot.setUpdatesListener(updates -> {
            updates.forEach(callback);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    @PostConstruct
    @Override
    public void startWebHookUpdates() {
        try {
            final Resource resource = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + "tg2vk.pem");
            SetWebhook request = new SetWebhook()
                    .url(pathResolver.getServerUrl() + PathConstants.API_TELEGRAM_FETCH_UPDATES)
                    .certificate(resource.getFile());
            bot.execute(request, loggerCallback());
        } catch (Exception e) {
            LOGGER.error("Error during webhook setup: ", e);
        }
    }

    @PreDestroy
    @Override
    public void stopWebHookUpdates() {
        try {
            SetWebhook request = new SetWebhook()
                    .url(StringUtils.EMPTY);
            bot.execute(request, loggerCallback());
        } catch (Exception e) {
            LOGGER.error("Error during webhook disabling: ", e);
        }
    }

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void send(T request) {
        bot.execute(request, loggerCallback());
    }

    private <T extends BaseRequest<T, R>, R extends BaseResponse> Callback<T, R> loggerCallback() {
        return new Callback<T, R>() {
            @Override
            public void onResponse(T request, R response) {
                LOGGER.trace("Successfully processed {}", request);
                LOGGER.trace("Telegram server response {}", response);
            }

            @Override
            public void onFailure(T request, IOException e) {
                LOGGER.error("Error during {} execution!", request);
                LOGGER.error(e.getMessage(), e);
            }
        };
    }
}
