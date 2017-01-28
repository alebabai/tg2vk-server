package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.security.service.JwtTokenFactoryService;
import com.github.alebabai.tg2vk.service.LinkerService;
import com.github.alebabai.tg2vk.service.PathResolverService;
import com.github.alebabai.tg2vk.service.TelegramService;
import com.github.alebabai.tg2vk.service.UserService;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.github.alebabai.tg2vk.util.constants.SecurityConstants;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.ChosenInlineResult;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.github.alebabai.tg2vk.util.CommandUtils.parseCommand;
import static com.github.alebabai.tg2vk.util.constants.CommandConstants.*;

@Service
public class TelegramUpdateHandlerServiceImpl extends AbstractTelegramUpdateHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramUpdateHandlerServiceImpl.class);

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
    protected void onInlineQueryReceived(InlineQuery query) {
        LOGGER.debug("Inline query received: {}", query);
    }

    @Override
    protected void onChosenInlineResultReceived(ChosenInlineResult queryResult) {
        LOGGER.debug("Chosen inline result received: {}", queryResult);

    }

    @Override
    protected void onCallbackQueryReceived(CallbackQuery callbackQuery) {
        LOGGER.debug("Callback query received: {}", callbackQuery);

    }

    @Override
    protected void onChanelPostReceived(Message post) {
        LOGGER.debug("Chanel post received: {}", post);

    }

    @Override
    protected void onEditedChanelPostReceived(Message post) {
        LOGGER.debug("Edited chanel post received: {}", post);

    }

    @Override
    protected void onEditedMessageReceived(Message message) {
        LOGGER.debug("Edited message received: {}", message);

    }

    @Override
    protected void onMessageReceived(Message message) {
        LOGGER.debug("Message received: {}", message);
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
        final String loginText = userService.findOneByTgId(context.from().id())
                .map(user -> String.join(
                        "\n\n",
                        messages.getMessage("tg.command.login.msg.warning", StringUtils.EMPTY),
                        messages.getMessage("tg.command.login.msg.instructions", StringUtils.EMPTY)
                ))
                .orElse(messages.getMessage("tg.command.login.msg.instructions", StringUtils.EMPTY));

        final SendMessage loginMessage = new SendMessage(context.chat().id(), loginText)
                .parseMode(ParseMode.Markdown)
                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                        new InlineKeyboardButton(messages.getMessage("tg.command.login.button.get_token.label", StringUtils.EMPTY))
                                .url(pathResolver.getAbsoluteUrl(PathConstants.API_AUTH_LOGIN)),
                        new InlineKeyboardButton(messages.getMessage("tg.command.login.button.send_token.label", StringUtils.EMPTY))
                                .url(String.join("#", pathResolver.getClientUrl(), tokenFactory.generate(context.from().id(), SecurityConstants.ROLE_USER))),
                }));
        tgService.send(loginMessage);
    }

    private void processStartCommand(Message context) {
        final Function<Optional<User>, String> messageCodeHandler = getMessageCodeHandler(
                "tg.command.start.user.already_started.msg",
                "tg.command.start.user.success.msg",
                "tg.command.start.anonymous.msg");
        processUserInitCommand(context, linkerService::start, messageCodeHandler);
    }

    private void processStopCommand(Message context) {
        final Function<Optional<User>, String> messageCodeHandler = getMessageCodeHandler(
                "tg.command.stop.user.success.msg",
                "tg.command.stop.user.already_stopped.msg",
                "tg.command.stop.anonymous.msg");
        processUserInitCommand(context, linkerService::stop, messageCodeHandler);
    }

    private Function<Optional<User>, String> getMessageCodeHandler(String startedCode, String stoppedCode, String anonymousCode) {
        return userOptional -> userOptional
                .map(user -> user.getSettings().isStarted() ? startedCode : stoppedCode)
                .orElse(anonymousCode);
    }

    private void processUserInitCommand(Message context, Consumer<User> userSpecificAction, Function<Optional<User>, String> getMessageCodeAction) {
        final Optional<User> userOptional = userService.findOneByTgId(context.from().id());
        final String messageCode = getMessageCodeAction.apply(userOptional);
        userOptional.ifPresent(userSpecificAction);
        final SendMessage message = new SendMessage(context.chat().id(), messages.getMessage(messageCode, StringUtils.EMPTY))
                .parseMode(ParseMode.Markdown);
        tgService.send(message);
    }


    private void processUnknownCommand(Message context) {
        SendMessage anyMessage = new SendMessage(context.chat().id(), messages.getMessage("tg.command.unknown.msg", StringUtils.EMPTY));
        tgService.send(anyMessage);
    }
}
