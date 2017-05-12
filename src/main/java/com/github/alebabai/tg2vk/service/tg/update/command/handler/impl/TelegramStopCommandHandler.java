package com.github.alebabai.tg2vk.service.tg.update.command.handler.impl;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.core.MessageFlowManager;
import com.github.alebabai.tg2vk.service.tg.common.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service("stop")
public class TelegramStopCommandHandler extends AbstractTelegramFlowCommandHandler {

    @Autowired
    public TelegramStopCommandHandler(MessageFlowManager flowManager,
                                      UserRepository userRepository,
                                      TelegramService tgService,
                                      MessageSourceAccessor messages) {
        super(flowManager, userRepository, tgService, messages);
    }

    @Override
    protected String getStartedCode() {
        return "tg.command.stop.msg.already_stopped";
    }

    @Override
    protected String getSuccessCode() {
        return "tg.command.stop.msg.success";
    }

    @Override
    protected String getAnonymousCode() {
        return "tg.command.stop.msg.anonymous";
    }

    @Override
    protected Consumer<User> getUserSpecificAction() {
        return flowManager::stop;
    }
}
