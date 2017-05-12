package com.github.alebabai.tg2vk.service.telegram.update.query.callback.impl;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.telegram.common.TelegramService;
import com.github.alebabai.tg2vk.service.telegram.update.query.callback.TelegramCallbackQueryProcessor;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
public class TelegramCallbackQueryProcessorImpl implements TelegramCallbackQueryProcessor {

    private final UserRepository userRepository;
    private final TelegramService tgService;
    private final MessageSourceAccessor messages;

    @Autowired
    public TelegramCallbackQueryProcessorImpl(UserRepository userRepository,
                                              TelegramService tgService,
                                              MessageSource messageSource) {
        this.userRepository = userRepository;
        this.tgService = tgService;
        this.messages = new MessageSourceAccessor(messageSource);
    }

    @Override
    public void process(CallbackQuery query) {
        final String data = query.data();
        final String[] tokens = StringUtils.split(data, "|");
        if (tokens.length >= 2) {
            final String type = tokens[0];
            final int vkChatId = NumberUtils.toInt(tokens[1]);
            switch (type) {
                case "link":
                    processChatLinkCallbackQuery(query.from().id(), vkChatId, query.id());
                    break;
                case "unlink":
                    final int tgChatId = NumberUtils.toInt(tokens[2]);
                    processChatUnlinkCallbackQuery(query.from().id(), tgChatId, vkChatId, query.id());
                    break;
                default:
                    break;
            }
        }
    }

    private void processChatLinkCallbackQuery(Integer tgUserId, Integer vkChatId, String queryId) {
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

    private void processChatUnlinkCallbackQuery(Integer tgUserId, Integer tgChatId, Integer vkChatId, String queryId) {
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
