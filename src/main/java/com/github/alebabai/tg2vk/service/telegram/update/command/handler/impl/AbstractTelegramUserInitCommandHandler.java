package com.github.alebabai.tg2vk.service.telegram.update.command.handler.impl;


import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.telegram.common.TelegramService;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractTelegramUserInitCommandHandler extends AbstractTelegramCommandHandler {

    protected final UserRepository userRepository;

    @Autowired
    public AbstractTelegramUserInitCommandHandler(TelegramService tgService, UserRepository userRepository, MessageSource messageSource) {
        super(tgService, messageSource);
        this.userRepository = userRepository;
    }


    protected Function<Optional<User>, String> getMessageCodeHandler(String startedCode, String stoppedCode, String anonymousCode) {
        return userOptional -> userOptional
                .map(user -> user.getSettings().isStarted() ? startedCode : stoppedCode)
                .orElse(anonymousCode);
    }

    protected void processUserInitCommand(Message context, Consumer<User> userSpecificAction, Function<Optional<User>, String> messageCodeHandler) {
        final Optional<User> userOptional = userRepository.findOneByTgId(context.from().id());
        final String messageCode = messageCodeHandler.apply(userOptional);
        userOptional.ifPresent(userSpecificAction);
        final SendMessage message = new SendMessage(context.chat().id(), messages.getMessage(messageCode))
                .parseMode(ParseMode.Markdown);
        tgService.send(message);
    }
}
