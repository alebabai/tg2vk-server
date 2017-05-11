package com.github.alebabai.tg2vk.service.telegram.update.command.handler.impl;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.core.MessageFlowManager;
import com.github.alebabai.tg2vk.service.telegram.common.TelegramService;
import com.github.alebabai.tg2vk.service.telegram.update.command.TelegramCommand;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service("start")
public class TelegramStartCommandHandler extends AbstractTelegramUserInitCommandHandler {

    private final MessageFlowManager flowManager;

    public TelegramStartCommandHandler(MessageFlowManager flowManager,
                                       TelegramService tgService,
                                       UserRepository userRepository,
                                       MessageSource messageSource) {
        super(tgService, userRepository, messageSource);
        this.flowManager = flowManager;
    }


    @Override
    public void handle(TelegramCommand command) {
        final Function<Optional<User>, String> messageCodeHandler = getMessageCodeHandler(
                "tg.command.start.msg.already_started",
                "tg.command.start.msg.success",
                "tg.command.start.msg.anonymous");
        processUserInitCommand(command.context(), flowManager::start, messageCodeHandler);
    }
}
