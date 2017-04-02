package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.domain.Chat;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.service.PathResolver;
import com.github.alebabai.tg2vk.service.TelegramService;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AbstractSendRequest;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteWebhook;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

@Service
public class TelegramServiceImpl implements TelegramService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramServiceImpl.class);

    @Value("${tg2vk.telegram.bot.token}")
    private String token;

    @Value("${tg2vk.telegram.bot.max_connections:40}")
    private Integer maxConnectionsCount;

    private final TelegramBot bot;
    private final PathResolver pathResolver;

    @Autowired
    public TelegramServiceImpl(PathResolver pathResolver) {
        this.bot = TelegramBotAdapter.build(token);
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
            final SetWebhook request = new SetWebhook()
                    .url(pathResolver.getAbsoluteUrl("/api/telegram/updates"))
                    .maxConnections(maxConnectionsCount);
            bot.execute(request, loggerCallback());
        } catch (Exception e) {
            LOGGER.error("Error during webhook setup: ", e);
        }
    }

    @PreDestroy
    @Override
    public void stopWebHookUpdates() {
        try {
            DeleteWebhook request = new DeleteWebhook();
            bot.execute(request, loggerCallback());
        } catch (Exception e) {
            LOGGER.error("Error during webhook disabling: ", e);
        }
    }

    @Override
    public <T extends AbstractSendRequest<T>> void send(T request) {
        bot.execute(request, loggerCallback());
    }

    @Override
    public Collection<Chat> getChats(User user) {
        return Collections.emptyList();
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
