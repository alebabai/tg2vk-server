package com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.impl;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.tg.common.TelegramService;
import com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.TelegramCallbackQuery;
import com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.TelegramCallbackQueryHandler;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Service("linkCallbackQueryHandler")
public class TelegramCallbackLinkQueryHandler implements TelegramCallbackQueryHandler {

    private final UserRepository userRepository;
    private final TelegramService tgService;
    private final MessageSourceAccessor messages;

    @Autowired
    public TelegramCallbackLinkQueryHandler(UserRepository userRepository,
                                            TelegramService tgService,
                                            MessageSourceAccessor messages) {
        this.userRepository = userRepository;
        this.tgService = tgService;
        this.messages = messages;
    }

    @Override
    public void handle(TelegramCallbackQuery query) {
        final Integer tgUserId = query.context().from().id();
        final Integer vkChatId = query.data().vkChatId();
        final String queryId = query.context().id();
        final String messageText = userRepository.findOneByTgId(tgUserId)
                .filter(user -> Objects.nonNull(user.getTempTgChatId()))
                .map(user -> {
                    final Integer tgChatId = user.getTempTgChatId();
                    final boolean alreadyExists = user.getChatsSettings().stream()
                            .anyMatch(it -> it.getTgChatId().equals(tgChatId) && it.getVkChatId().equals(vkChatId));
                    if (!alreadyExists) {
                        user.setTempTgChatId(null);
                        user.getChatsSettings().add(new ChatSettings(tgChatId, vkChatId).setStarted(true));
                        userRepository.save(user);
                        return messages.getMessage("tg.callback.chats.link.msg.success");
                    }
                    return messages.getMessage("tg.callback.chats.link.msg.already_exists");
                })
                .orElse(messages.getMessage("tg.callback.chats.link.msg.denied"));
        final AnswerCallbackQuery message = new AnswerCallbackQuery(queryId)
                .text(messageText)
                .showAlert(true);
        tgService.send(message);
    }
}
