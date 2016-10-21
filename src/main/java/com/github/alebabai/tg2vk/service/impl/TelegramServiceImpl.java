package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.TelegramService;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.GetUpdatesListener;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.function.Function;

@Service
public class TelegramServiceImpl implements TelegramService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramService.class);

    @Autowired
    private TelegramBot bot;

    @Override
    public void fetchUpdates(Function<? super Update, ?> callback) {
        bot.setGetUpdatetsListener(updates -> {
            Update update = updates.get(0);
            LOGGER.trace("Processing {0}", update);
            callback.apply(update);
            return GetUpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> void send(T request) {
        bot.execute(request, loggerCallback());
    }

    private <T extends BaseRequest<T, R>, R extends BaseResponse> Callback<T, R> loggerCallback() {
        return new Callback<T, R>() {
            @Override
            public void onResponse(T request, R response) {
                LOGGER.trace("Request {0}", request);
                LOGGER.trace("Response {0}", response);
            }

            @Override
            public void onFailure(T request, IOException e) {
                LOGGER.error("Error during {0} execution!", request);
                LOGGER.error(e.getMessage(), e);
            }
        };
    }

}
