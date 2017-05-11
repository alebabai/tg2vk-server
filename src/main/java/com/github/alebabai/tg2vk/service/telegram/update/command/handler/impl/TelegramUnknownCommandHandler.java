package com.github.alebabai.tg2vk.service.telegram.update.command.handler.impl;

import com.github.alebabai.tg2vk.service.telegram.common.TelegramService;
import com.github.alebabai.tg2vk.service.telegram.update.command.TelegramCommand;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service("unknown")
public class TelegramUnknownCommandHandler extends AbstractTelegramCommandHandler {

    @Autowired
    public TelegramUnknownCommandHandler(TelegramService tgService, MessageSource messageSource) {
        super(tgService, messageSource);
    }

    @Override
    public void handle(TelegramCommand command) {
        SendMessage anyMessage = new SendMessage(command.context().chat().id(), messages.getMessage("tg.command.unknown.msg"));
        tgService.send(anyMessage);
    }
}
