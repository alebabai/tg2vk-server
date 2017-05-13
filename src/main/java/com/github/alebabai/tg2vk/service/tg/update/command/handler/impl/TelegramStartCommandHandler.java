package com.github.alebabai.tg2vk.service.tg.update.command.handler.impl;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.core.MessageFlowManager;
import com.github.alebabai.tg2vk.service.tg.common.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service("startCommandHandler")
public class TelegramStartCommandHandler extends AbstractTelegramFlowCommandHandler {

    @Autowired
    public TelegramStartCommandHandler(MessageFlowManager flowManager,
                                       UserRepository userRepository,
                                       TelegramService tgService,
                                       MessageSourceAccessor messages) {
        super(flowManager, userRepository, tgService, messages);
    }

    @Override
    protected String getCodePrefix() {
        return "tg.command.start.msg.";
    }

    @Override
    protected Consumer<User> getUserSpecificAction() {
        return flowManager::start;
    }
}
