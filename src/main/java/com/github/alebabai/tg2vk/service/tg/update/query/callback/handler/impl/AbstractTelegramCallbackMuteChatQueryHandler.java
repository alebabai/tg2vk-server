package com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.impl;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.tg.common.TelegramService;
import com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.TelegramCallbackQuery;
import com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.TelegramCallbackQueryHandler;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Objects;

public abstract class AbstractTelegramCallbackMuteChatQueryHandler implements TelegramCallbackQueryHandler {
    private final UserRepository userRepository;
    private final TelegramService tgService;
    private final MessageSourceAccessor messages;

    public AbstractTelegramCallbackMuteChatQueryHandler(UserRepository userRepository,
                                                        TelegramService tgService,
                                                        MessageSourceAccessor messages) {
        this.userRepository = userRepository;
        this.tgService = tgService;
        this.messages = messages;
    }

    protected abstract String getCodePrefix();

    protected abstract boolean getState();

    protected abstract boolean isProcessable(ChatSettings chatSettings);

    @Override
    public void handle(TelegramCallbackQuery query) {
        final CallbackQuery context = query.context();
        final Integer tgUserId = context.from().id();
        final Integer tgChatId = query.data().tgChatId();
        final Integer vkChatId = query.data().vkChatId();
        final String queryId = context.id();
        final String messageText = userRepository.findOneByTgId(tgUserId)
                .map(user -> user.getChatsSettings().stream()
                        .filter(it -> Objects.equals(it.getTgChatId(), tgChatId) && Objects.equals(it.getVkChatId(), vkChatId))
                        .findAny()
                        .filter(this::isProcessable)
                        .map(it -> {
                            it.setStarted(getState());
                            userRepository.save(user);
                            return messages.getMessage(getCodePrefix() + "success");
                        })
                        .orElseGet(() -> messages.getMessage(getCodePrefix() + "already"))
                )
                .orElse(messages.getMessage(getCodePrefix() + "denied"));
        final AnswerCallbackQuery message = new AnswerCallbackQuery(queryId)
                .text(messageText)
                .showAlert(true);
        tgService.send(message);
    }

}
