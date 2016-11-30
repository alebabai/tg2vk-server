package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.LinkerService;
import com.github.alebabai.tg2vk.service.PathResolverService;
import com.github.alebabai.tg2vk.service.TelegramService;
import com.github.alebabai.tg2vk.service.TelegramUpdateHandlerService;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TelegramUpdateHandlerServiceImpl implements TelegramUpdateHandlerService {

    private final TelegramService tgService;
    private final PathResolverService pathResolver;
    private final LinkerService linkerService;
    private final Environment env;


    @Autowired
    private TelegramUpdateHandlerServiceImpl(TelegramService tgService,
                                             PathResolverService pathResolver,
                                             LinkerService linkerService,
                                             Environment env) {
        this.tgService = tgService;
        this.pathResolver = pathResolver;
        this.linkerService = linkerService;
        this.env = env;
    }

    @Override
    public void handle(Update update) {
        mainHandler(update);
    }

    @Override
    public void handleAsync(Update update) {
        CompletableFuture.runAsync(() -> mainHandler(update));
    }

    private void mainHandler(Update update) {
        if (update == null) {
            return;
        }

        if (update.inlineQuery() != null) {
            TelegramUpdateHandlerServiceImpl.this.handleInlineQuery(update.inlineQuery());
        }

        if (update.chosenInlineResult() != null) {
            TelegramUpdateHandlerServiceImpl.this.handleChosenInlineResult(update.chosenInlineResult());
        }

        if (update.callbackQuery() != null) {
            TelegramUpdateHandlerServiceImpl.this.handleCallbackQuery(update.callbackQuery());
        }

        if (update.channelPost() != null) {
            TelegramUpdateHandlerServiceImpl.this.handleChanelPost(update.channelPost());
        }

        if (update.editedChannelPost() != null) {
            TelegramUpdateHandlerServiceImpl.this.handleEditedChanelPost(update.editedChannelPost());
        }

        if (update.message() != null) {
            TelegramUpdateHandlerServiceImpl.this.handleMessage(update.message());
        }

        if (update.editedMessage() != null) {
            TelegramUpdateHandlerServiceImpl.this.handleEditedMessage(update.editedMessage());
        }
    }

    private void handleInlineQuery(InlineQuery query) {
    }

    private void handleChosenInlineResult(ChosenInlineResult queryResult) {
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
    }

    private void handleChanelPost(Message post) {
    }

    private void handleEditedChanelPost(Message post) {
    }

    private void handleMessage(Message message) {
        switch (message.text()) {
            case "/login":
                SendMessage loginMessage = new SendMessage(message.chat().id(), "Test Login")
                        .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                                new InlineKeyboardButton("Login").url(pathResolver.getServerUrl() + PathConstants.API_LOGIN)
                        }));
                tgService.send(loginMessage);
                break;
            case "/start":
                final com.github.alebabai.tg2vk.domain.User user = new com.github.alebabai.tg2vk.domain.User()
                        .setVkId(env.getProperty("vk_user_id", Integer.TYPE))
                        .setTgId(env.getProperty("tg_user_id", Integer.TYPE))
                        .setVkToken(env.getProperty("token"));
                linkerService.start(user);
                final SendMessage startMessage = new SendMessage(message.chat().id(), "VK updates fetching started");
                tgService.send(startMessage);
                break;
            case "/stop":
                linkerService.stop();
                final SendMessage stopMessage = new SendMessage(message.chat().id(), "VK updates fetching stopped");
                tgService.send(stopMessage);
                break;
            default:
                SendMessage anyMessage = new SendMessage(message.chat().id(), message.text());
                tgService.send(anyMessage);
                break;
        }
    }

    private void handleEditedMessage(Message message) {
    }

}
