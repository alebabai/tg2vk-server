package com.github.alebabai.tg2vk.service.tg.update.query.inline.impl;

import com.github.alebabai.tg2vk.domain.Chat;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.tg.common.TelegramService;
import com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.TelegramCallbackQueryData;
import com.github.alebabai.tg2vk.service.tg.update.query.inline.TelegramInlineQueryProcessor;
import com.github.alebabai.tg2vk.service.vk.VkService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import static java.util.stream.Collectors.toList;

@Service
public class TelegramInlineQueryProcessorImpl implements TelegramInlineQueryProcessor {

    private final UserRepository userRepository;
    private final TelegramService tgService;
    private final VkService vkService;
    private final MessageSourceAccessor messages;
    private final Gson gson;

    @Autowired
    public TelegramInlineQueryProcessorImpl(UserRepository userRepository,
                                            TelegramService tgService,
                                            VkService vkService,
                                            MessageSource messageSource) {
        this.userRepository = userRepository;
        this.tgService = tgService;
        this.vkService = vkService;
        this.messages = new MessageSourceAccessor(messageSource);
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void process(InlineQuery query) {
        processVkChatsInlineQuery(query);
    }

    private void processVkChatsInlineQuery(InlineQuery query) {
        final AnswerInlineQuery answerInlineQuery = userRepository.findOneByTgId(query.from().id())
                .map(user -> vkService.findChats(user, query.query()).stream()
                        .map(chat -> new InlineQueryResultArticle(chat.getId().toString(), chat.getTitle(), chat.getTitle())
                                .thumbUrl(chat.getThumbUrl())
                                .description(messages.getMessage("tg.inline.chats." + StringUtils.lowerCase(chat.getType().toString())))
                                .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                                        new InlineKeyboardButton(messages.getMessage("tg.inline.chats.link.label.button"))
                                                .callbackData(createCallbackData(chat))
                                }))
                                .inputMessageContent(new InputTextMessageContent(String.format("*%s*%n%s", chat.getTitle(), messages.getMessage("tg.inline.chats.link.msg.confirm")))
                                        .parseMode(ParseMode.Markdown)))
                        .collect(toList()))
                .map(queryResults -> new AnswerInlineQuery(query.id(), queryResults.toArray(new InlineQueryResult[0]))
                        .isPersonal(true))
                .orElseGet(() -> new AnswerInlineQuery(query.id()).isPersonal(true));
        tgService.send(answerInlineQuery);
    }

    private String createCallbackData(Chat chat) {
        return gson.toJson(TelegramCallbackQueryData.builder().type("link")
                .vkChatId(chat.getId()).build());
    }
}
