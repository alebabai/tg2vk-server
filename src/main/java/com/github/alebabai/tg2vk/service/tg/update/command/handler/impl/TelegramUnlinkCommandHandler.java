package com.github.alebabai.tg2vk.service.tg.update.command.handler.impl;

import com.github.alebabai.tg2vk.domain.Chat;
import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.tg.common.TelegramService;
import com.github.alebabai.tg2vk.service.tg.update.command.TelegramCommand;
import com.github.alebabai.tg2vk.service.tg.update.command.handler.TelegramCommandHandler;
import com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.TelegramCallbackQueryData;
import com.github.alebabai.tg2vk.service.vk.VkService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

@Service("unlink")
public class TelegramUnlinkCommandHandler implements TelegramCommandHandler {

    private final UserRepository userRepository;
    private final TelegramService tgService;
    private final VkService vkService;
    private final MessageSourceAccessor messages;
    private final Gson gson;

    public TelegramUnlinkCommandHandler(UserRepository userRepository, TelegramService tgService, VkService vkService, MessageSourceAccessor messages) {
        this.userRepository = userRepository;
        this.tgService = tgService;
        this.vkService = vkService;
        this.messages = messages;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void handle(TelegramCommand command) {
        final Message context = command.context();
        final Integer tgChatId = Math.toIntExact(context.chat().id());
        final SendMessage message = userRepository.findOneByTgId(context.from().id())
                .map(user -> {
                    user.setTempTgChatId(Math.toIntExact(tgChatId));
                    final List<Integer> vkChatIds = user.getChatsSettings().stream()
                            .filter(chatSettings -> Objects.equals(chatSettings.getTgChatId(), tgChatId))
                            .map(ChatSettings::getVkChatId)
                            .collect(toList());
                    final InlineKeyboardButton[] buttons = of(vkChatIds)
                            .filter(ids -> !ids.isEmpty())
                            .map(ids -> vkService.resolveChats(user).stream()
                                    .filter(chat -> ids.contains(chat.getId()))
                                    .map(chat -> createInlineKeyboardButton(tgChatId, chat))
                                    .collect(toList())
                                    .toArray(new InlineKeyboardButton[0]))
                            .orElse(new InlineKeyboardButton[0]);
                    final String code = vkChatIds.isEmpty() ? "tg.command.unlink.msg.no_links" : "tg.command.unlink.msg.info";
                    return new SendMessage(tgChatId, messages.getMessage(code))
                            .parseMode(ParseMode.Markdown)
                            .replyMarkup(new InlineKeyboardMarkup(buttons));
                })
                .orElseGet(() -> new SendMessage(tgChatId, messages.getMessage("tg.command.unlink.msg.denied")));
        tgService.send(message);
    }

    private InlineKeyboardButton createInlineKeyboardButton(Integer tgChatId, Chat chat) {
        final TelegramCallbackQueryData data = TelegramCallbackQueryData.builder()
                .type("unlink")
                .tgChatId(tgChatId)
                .vkChatId(chat.getId())
                .build();
        final String json = gson.toJson(data);
        return new InlineKeyboardButton(chat.getTitle()).callbackData(json);
    }
}
