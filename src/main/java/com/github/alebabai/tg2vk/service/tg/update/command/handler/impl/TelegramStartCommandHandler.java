package com.github.alebabai.tg2vk.service.tg.update.command.handler.impl;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.core.MessageFlowManager;
import com.github.alebabai.tg2vk.service.tg.common.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service("start")
public class TelegramStartCommandHandler extends AbstractTelegramFlowCommandHandler {

    @Autowired
    public TelegramStartCommandHandler(MessageFlowManager flowManager,
                                       UserRepository userRepository,
                                       TelegramService tgService,
                                       MessageSourceAccessor messages) {
        super(flowManager, userRepository, tgService, messages);
    }

    @Override
    protected String getStartedCode() {
        return "tg.command.start.msg.already_started";
    }

    @Override
    protected String getSuccessCode() {
        return "tg.command.start.msg.success";
    }

    @Override
    protected String getAnonymousCode() {
        return "tg.command.start.msg.anonymous";
    }

    @Override
    protected Consumer<User> getUserSpecificAction() {
        return flowManager::start;
    }
}
