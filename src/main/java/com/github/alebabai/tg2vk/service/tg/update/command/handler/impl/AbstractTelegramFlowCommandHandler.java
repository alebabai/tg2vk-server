package com.github.alebabai.tg2vk.service.tg.update.command.handler.impl;


import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.core.MessageFlowManager;
import com.github.alebabai.tg2vk.service.tg.common.TelegramService;
import com.github.alebabai.tg2vk.service.tg.update.command.TelegramCommand;
import com.github.alebabai.tg2vk.service.tg.update.command.handler.TelegramCommandHandler;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractTelegramFlowCommandHandler implements TelegramCommandHandler {

    protected final MessageFlowManager flowManager;
    private final UserRepository userRepository;
    private final TelegramService tgService;
    private final MessageSourceAccessor messages;

    public AbstractTelegramFlowCommandHandler(MessageFlowManager flowManager,
                                              UserRepository userRepository,
                                              TelegramService tgService,
                                              MessageSourceAccessor messages) {
        this.userRepository = userRepository;
        this.tgService = tgService;
        this.flowManager = flowManager;
        this.messages = messages;
    }

    protected abstract String getCodePrefix();

    protected abstract Consumer<User> getUserSpecificAction();

    @Override
    public void handle(TelegramCommand command) {
        final Function<Optional<User>, String> messageCodeHandler = getMessageCodeHandler(
                getCodePrefix() + "already",
                getCodePrefix() + "success",
                getCodePrefix() + "anonymous");
        processUserInitCommand(command.context(), getUserSpecificAction(), messageCodeHandler);
    }

    private Function<Optional<User>, String> getMessageCodeHandler(String alreadyCode, String stoppedCode, String anonymousCode) {
        return userOptional -> userOptional
                .map(user -> user.getSettings().isStarted() ? alreadyCode : stoppedCode)
                .orElse(anonymousCode);
    }

    private void processUserInitCommand(Message context, Consumer<User> userSpecificAction, Function<Optional<User>, String> messageCodeHandler) {
        final Optional<User> userOptional = userRepository.findOneByTgId(context.from().id());
        final String messageCode = messageCodeHandler.apply(userOptional);
        userOptional.ifPresent(userSpecificAction);
        final SendMessage message = new SendMessage(context.chat().id(), messages.getMessage(messageCode))
                .parseMode(ParseMode.Markdown);
        tgService.send(message);
    }
}
