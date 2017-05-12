package com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.impl;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.tg.common.TelegramService;
import com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.TelegramCallbackQuery;
import com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.TelegramCallbackQueryHandler;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service("unlinkCallbackQueryHandler")
public class TelegramCallbackUnlinkQueryHandler implements TelegramCallbackQueryHandler {

    private final UserRepository userRepository;
    private final TelegramService tgService;
    private final MessageSourceAccessor messages;

    @Autowired
    public TelegramCallbackUnlinkQueryHandler(UserRepository userRepository,
                                              TelegramService tgService,
                                              MessageSourceAccessor messages) {
        this.userRepository = userRepository;
        this.tgService = tgService;
        this.messages = messages;
    }

    @Override
    public void handle(TelegramCallbackQuery query) {
        final CallbackQuery context = query.context();
        final Integer tgUserId = context.from().id();
        final Integer tgChatId = query.data().tgChatId();
        final Integer vkChatId = query.data().vkChatId();
        final String queryId = context.id();
        final String messageText = userRepository.findOneByTgId(tgUserId)
                .map(user -> {
                    final Set<ChatSettings> chatSettings = user.getChatsSettings().stream()
                            .filter(it -> !(Objects.equals(it.getTgChatId(), tgChatId) && Objects.equals(it.getVkChatId(), vkChatId)))
                            .collect(toSet());
                    if (chatSettings.size() < user.getChatsSettings().size()) {
                        user.setChatsSettings(chatSettings);
                        userRepository.save(user);
                        return messages.getMessage("tg.callback.chats.unlink.msg.success");
                    }
                    return messages.getMessage("tg.callback.chats.unlink.msg.already_unlinked");
                })
                .orElse(messages.getMessage("tg.callback.chats.unlink.msg.denied"));
        final AnswerCallbackQuery message = new AnswerCallbackQuery(queryId)
                .text(messageText)
                .showAlert(true);
        tgService.send(message);
    }
}
