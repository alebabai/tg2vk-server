package com.github.alebabai.tg2vk.service.telegram.update.command.handler.impl;


import com.github.alebabai.tg2vk.domain.Role;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.security.service.JwtTokenFactoryService;
import com.github.alebabai.tg2vk.service.core.PathResolver;
import com.github.alebabai.tg2vk.service.telegram.common.TelegramService;
import com.github.alebabai.tg2vk.service.telegram.update.command.handler.TelegramCommandHandler;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class AbstractTelegramClientRedirectCommandHandler implements TelegramCommandHandler {

    protected final UserRepository userRepository;
    protected final TelegramService tgService;
    protected final PathResolver pathResolver;
    protected final JwtTokenFactoryService tokenFactory;
    protected final MessageSourceAccessor messages;

    @Autowired
    public AbstractTelegramClientRedirectCommandHandler(UserRepository userRepository,
                                                        PathResolver pathResolver,
                                                        TelegramService tgService,
                                                        JwtTokenFactoryService tokenFactory,
                                                        MessageSource messageSource) {
        this.userRepository = userRepository;
        this.pathResolver = pathResolver;
        this.tgService = tgService;
        this.tokenFactory = tokenFactory;
        this.messages = new MessageSourceAccessor(messageSource);
    }

    protected String getClientRedirectUrl(String token, String clientRoute) {
        return UriComponentsBuilder
                .fromUriString(pathResolver.resolveServerUrl("/api/redirect/client"))
                .pathSegment(clientRoute)
                .queryParam("token", token)
                .toUriString();
    }

    @Data
    @NoArgsConstructor
    @Accessors(fluent = true)
    protected class ClientRedirectSendMessageBuilder {
        private Message context;
        private String clientRoute;
        private String normalMessageCode;
        private String anonymousMessageCode;
        private String buttonLabelCode;

        SendMessage build() {
            return userRepository.findOneByTgId(context.from().id())
                    .map(user -> {
                        final Role[] roles = user.getRoles().toArray(new Role[0]);
                        final String text = messages.getMessage(normalMessageCode);
                        return new SendMessage(context.chat().id(), text)
                                .parseMode(ParseMode.Markdown)
                                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                                        new InlineKeyboardButton(messages.getMessage(buttonLabelCode))
                                                .url(getClientRedirectUrl(tokenFactory.generate(context.from().id(), roles), clientRoute)),
                                }));
                    })
                    .orElseGet(() -> new SendMessage(context.chat().id(), messages.getMessage(anonymousMessageCode)));
        }
    }
}
