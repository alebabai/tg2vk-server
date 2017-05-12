package com.github.alebabai.tg2vk.service.telegram.update.command.handler.impl;

import com.github.alebabai.tg2vk.domain.Role;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.security.service.JwtTokenFactoryService;
import com.github.alebabai.tg2vk.service.core.PathResolver;
import com.github.alebabai.tg2vk.service.telegram.common.TelegramService;
import com.github.alebabai.tg2vk.service.telegram.update.command.TelegramCommand;
import com.github.alebabai.tg2vk.service.telegram.update.command.handler.TelegramCommandHandler;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.github.alebabai.tg2vk.util.CommandUtils.getClientRedirectUrl;
import static java.util.Optional.of;

@Service("login")
public class TelegramLoginCommandHandler implements TelegramCommandHandler {

    private final UserRepository userRepository;
    private final PathResolver pathResolver;
    private final TelegramService tgService;
    private final JwtTokenFactoryService tokenFactory;
    private final MessageSourceAccessor messages;

    @Autowired
    public TelegramLoginCommandHandler(UserRepository userRepository,
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
        final SendMessage loginMessage = of(context.chat().type())
                .filter(Chat.Type.Private::equals)
                .map(type -> createLoginSendMessage(context))
                .orElseGet(() -> createAccessDeniedSendMessage(context.chat().id(), messages.getMessage("tg.command.login.msg.denied")));
        tgService.send(loginMessage);
    }

    private SendMessage createAccessDeniedSendMessage(Long id, String message2) {
        return new SendMessage(id, message2);
    }

    private SendMessage createLoginSendMessage(Message message) {
        final Optional<User> userOptional = userRepository.findOneByTgId(message.from().id());
        final String loginText = userOptional
                .map(user -> String.join(
                        "\n\n",
                        messages.getMessage("tg.command.login.msg.warning"),
                        messages.getMessage("tg.command.login.msg.instructions")
                ))
                .orElse(messages.getMessage("tg.command.login.msg.instructions"));

        final Role[] roles = userOptional
                .map(user -> user.getRoles().toArray(new Role[0]))
                .orElse(new Role[]{Role.USER});

        return createAccessDeniedSendMessage(message.chat().id(), loginText)
                .parseMode(ParseMode.Markdown)
                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                        new InlineKeyboardButton(messages.getMessage("tg.command.login.label.button.get_token"))
                                .url(pathResolver.resolveServerUrl("/api/redirect/vk-login")),
                        new InlineKeyboardButton(messages.getMessage("tg.command.login.label.button.send_token"))
                                .url(getClientRedirectUrl(tokenFactory.generate(message.from().id(), roles), pathResolver.resolveServerUrl("/api/redirect/client"), "revoke-token")),
                }));
    }
}
