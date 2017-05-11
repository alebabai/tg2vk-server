package com.github.alebabai.tg2vk.service.telegram.update.command.handler.impl;


import com.github.alebabai.tg2vk.service.telegram.common.TelegramService;
import com.github.alebabai.tg2vk.service.telegram.update.command.handler.TelegramCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

public abstract class AbstractTelegramCommandHandler implements TelegramCommandHandler {

    protected final TelegramService tgService;
    protected final MessageSourceAccessor messages;

    @Autowired
    public AbstractTelegramCommandHandler(TelegramService tgService,
                                          MessageSource messageSource) {
        this.tgService = tgService;
        this.messages = new MessageSourceAccessor(messageSource);
    }
}
