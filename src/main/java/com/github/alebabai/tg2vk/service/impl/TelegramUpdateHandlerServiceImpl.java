package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.service.LinkerService;
import com.github.alebabai.tg2vk.service.PathResolverService;
import com.github.alebabai.tg2vk.service.TelegramService;
import com.github.alebabai.tg2vk.service.TelegramUpdateHandlerService;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.github.alebabai.tg2vk.util.CommandUtils.*;

@Service
public class TelegramUpdateHandlerServiceImpl implements TelegramUpdateHandlerService {

    private final TelegramService tgService;
    private final PathResolverService pathResolver;
    private final LinkerService linkerService;
    private final Environment env;
    private final MessageSourceAccessor messages;
    private final User user;//TODO remove

    @Autowired
    public TelegramUpdateHandlerServiceImpl(TelegramService tgService,
                                            PathResolverService pathResolver,
                                            LinkerService linkerService,
                                            MessageSource messageSource,
                                            Environment env) {
        this.tgService = tgService;
        this.pathResolver = pathResolver;
        this.linkerService = linkerService;
        this.messages = new MessageSourceAccessor(messageSource);
        this.env = env;

        //TODO remove
        this.user = new User()
                .setVkId(env.getProperty("vk_user_id", Integer.TYPE))
                .setTgId(env.getProperty("tg_user_id", Integer.TYPE))
                .setVkToken(env.getProperty("token"));
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

    private void handleEditedMessage(Message message) {
    }

    private void handleMessage(Message message) {
        if (message.text().startsWith("/")) {
            parseCommand(message.text(), (command, args) -> processCommand(command, args, message));
        } else {
            SendMessage anyMessage = new SendMessage(message.chat().id(), message.text());
            tgService.send(anyMessage);
        }
    }

    private void processCommand(String command, List<String> args, Message context) {
        switch (command) {
            case COMMAND_LOGIN:
                SendMessage loginMessage = new SendMessage(context.chat().id(), messages.getMessage("tg.command.login.msg", StringUtils.EMPTY))
                        .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                                new InlineKeyboardButton(messages.getMessage("tg.command.login.button.label", StringUtils.EMPTY))
                                        .url(pathResolver.getServerUrl() + PathConstants.API_AUTH_LOGIN)
                        }));
                tgService.send(loginMessage);
                break;
            case COMMAND_START:
                linkerService.start(user);//TODO get user from from repository by tg id
                final SendMessage startMessage = new SendMessage(context.chat().id(), messages.getMessage("tg.command.start.msg", StringUtils.EMPTY));
                tgService.send(startMessage);
                break;
            case COMMAND_STOP:
                linkerService.stop(user);//TODO get user from from repository by tg id
                final SendMessage stopMessage = new SendMessage(context.chat().id(), messages.getMessage("tg.command.stop.msg", StringUtils.EMPTY));
                tgService.send(stopMessage);
                break;
            default:
                SendMessage anyMessage = new SendMessage(context.chat().id(), messages.getMessage("tg.command.unknown.msg", StringUtils.EMPTY));
                tgService.send(anyMessage);
                break;
        }
    }
}
