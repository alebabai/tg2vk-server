package com.github.alebabai.tg2vk.service.telegram.update.command.handler.impl;

import com.github.alebabai.tg2vk.domain.Role;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.security.service.JwtTokenFactoryService;
import com.github.alebabai.tg2vk.service.core.PathResolver;
import com.github.alebabai.tg2vk.service.telegram.common.TelegramService;
import com.github.alebabai.tg2vk.service.telegram.update.command.TelegramCommand;
import com.github.alebabai.tg2vk.service.telegram.update.command.handler.TelegramCommandHandler;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import static com.github.alebabai.tg2vk.util.CommandUtils.getClientRedirectUrl;

@Service("settings")
public class TelegramSettingsCommandHandler implements TelegramCommandHandler {

    private final UserRepository userRepository;
    private final PathResolver pathResolver;
    private final TelegramService tgService;
    private final JwtTokenFactoryService tokenFactory;
    private final MessageSourceAccessor messages;

    @Autowired
    public TelegramSettingsCommandHandler(UserRepository userRepository,
                                          PathResolver pathResolver,
                                          TelegramService tgService,
                                          JwtTokenFactoryService tokenFactory,
                                          MessageSourceAccessor messages) {
        this.userRepository = userRepository;
        this.pathResolver = pathResolver;
        this.tgService = tgService;
        this.tokenFactory = tokenFactory;
        this.messages = messages;
    }

    @Override
    public void handle(TelegramCommand command) {
        final Message context = command.context();
        final SendMessage message = userRepository.findOneByTgId(context.from().id())
                .map(user -> {
                    final Role[] roles = user.getRoles().toArray(new Role[0]);
                    final String text = messages.getMessage("tg.command.settings.msg.info");
                    return new SendMessage(context.chat().id(), text)
                            .parseMode(ParseMode.Markdown)
                            .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                                    new InlineKeyboardButton(messages.getMessage("tg.command.settings.label.button.open"))
                                            .url(getClientRedirectUrl(tokenFactory.generate(context.from().id(), roles), pathResolver.resolveServerUrl("/api/redirect/client"), "settings")),
                            }));
                })
                .orElseGet(() -> new SendMessage(context.chat().id(), messages.getMessage("tg.command.settings.msg.denied")));
        tgService.send(message);
    }


}
