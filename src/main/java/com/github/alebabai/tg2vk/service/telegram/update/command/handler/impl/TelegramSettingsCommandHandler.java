package com.github.alebabai.tg2vk.service.telegram.update.command.handler.impl;

import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.security.service.JwtTokenFactoryService;
import com.github.alebabai.tg2vk.service.core.PathResolver;
import com.github.alebabai.tg2vk.service.telegram.common.TelegramService;
import com.github.alebabai.tg2vk.service.telegram.update.command.TelegramCommand;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service("settings")
public class TelegramSettingsCommandHandler extends AbstractTelegramClientRedirectCommandHandler {


    @Autowired
    public TelegramSettingsCommandHandler(UserRepository userRepository,
                                          PathResolver pathResolver,
                                          TelegramService tgService,
                                          JwtTokenFactoryService tokenFactory,
                                          MessageSource messageSource) {
        super(userRepository, pathResolver, tgService, tokenFactory, messageSource);
    }

    @Override
    public void handle(TelegramCommand command) {
        final SendMessage message = new ClientRedirectSendMessageBuilder()
                .context(command.context())
                .clientRoute("settings")
                .normalMessageCode("tg.command.settings.msg.info")
                .anonymousMessageCode("tg.command.settings.msg.denied")
                .buttonLabelCode("tg.command.settings.label.button.open")
                .build();
        tgService.send(message);
    }
}
