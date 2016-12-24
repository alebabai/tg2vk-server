package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.security.service.JwtTokenFactoryService;
import com.github.alebabai.tg2vk.service.*;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.github.alebabai.tg2vk.util.CommandUtils.parseCommand;
import static com.github.alebabai.tg2vk.util.constants.CommandConstants.*;

//TODO refactor this class (use composition)
@Service
public class TelegramUpdateHandlerServiceImpl implements TelegramUpdateHandlerService {

    private final UserService userService;
    private final TelegramService tgService;
    private final PathResolverService pathResolver;
    private final LinkerService linkerService;
    private final JwtTokenFactoryService tokenFactory;
    private final MessageSourceAccessor messages;

    @Autowired
    public TelegramUpdateHandlerServiceImpl(PathResolverService pathResolver,
                                            UserService userService,
                                            TelegramService tgService,
                                            LinkerService linkerService,
                                            JwtTokenFactoryService tokenFactory,
                                            MessageSource messageSource) {
        this.pathResolver = pathResolver;
        this.userService = userService;
        this.tgService = tgService;
        this.linkerService = linkerService;
        this.tokenFactory = tokenFactory;
        this.messages = new MessageSourceAccessor(messageSource);
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
                processLoginCommand(context);
                break;
            case COMMAND_START:
                processStartCommand(context);
                break;
            case COMMAND_STOP:
                processStopCommand(context);
                break;
            default:
                processUnknownCommand(context);
                break;
        }
    }

    private void processLoginCommand(Message context) {
        SendMessage loginMessage = new SendMessage(context.chat().id(), messages.getMessage("tg.command.login.msg", StringUtils.EMPTY))
                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                        new InlineKeyboardButton(messages.getMessage("tg.command.login.button.label", StringUtils.EMPTY))
                                .url(pathResolver.getAbsoluteUrl(PathConstants.API_AUTH_LOGIN))
                }));//TODO implement login command according to the specifications
        tgService.send(loginMessage);
    }

    private void processStartCommand(Message context) {
        userService.findOneByTgId(context.from().id())
                .ifPresent(user -> {
                    linkerService.start(user);
                    final SendMessage startMessage = new SendMessage(context.chat().id(), messages.getMessage("tg.command.start.msg", StringUtils.EMPTY));
                    tgService.send(startMessage);
                });//TODO implement start command according to the specifications
    }

    private void processStopCommand(Message context) {
        userService.findOneByTgId(context.from().id())
                .ifPresent(user -> {
                    linkerService.stop(user);
                    final SendMessage stopMessage = new SendMessage(context.chat().id(), messages.getMessage("tg.command.stop.msg", StringUtils.EMPTY));
                    tgService.send(stopMessage);
                });//TODO implement stop command according to the specifications
    }


    private void processUnknownCommand(Message context) {
        SendMessage anyMessage = new SendMessage(context.chat().id(), messages.getMessage("tg.command.unknown.msg", StringUtils.EMPTY));
        tgService.send(anyMessage);
    }
}
