package com.github.alebabai.tg2vk.service.tg.update.command.handler.impl;

import com.github.alebabai.tg2vk.domain.Chat;
import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.domain.User;
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

import java.util.List;
import java.util.Objects;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.ListUtils.partition;

public abstract class AbstractTelegramChatsCommandHandler implements TelegramCommandHandler {

    private static final int BUTTONS_COLUMNS_COUNT = 3;

    private final UserRepository userRepository;
    private final TelegramService tgService;
    private final VkService vkService;
    private final MessageSourceAccessor messages;
    private final Gson gson;

    public AbstractTelegramChatsCommandHandler(UserRepository userRepository, TelegramService tgService, VkService vkService, MessageSourceAccessor messages) {
        this.userRepository = userRepository;
        this.tgService = tgService;
        this.vkService = vkService;
        this.messages = messages;
        this.gson = new GsonBuilder().create();
    }

    protected abstract String getCodePrefix();

    protected abstract TelegramCallbackQueryData getCallbackData(Integer tgChatId, Chat chat);

    @Override
    public void handle(TelegramCommand command) {
        final Message context = command.context();
        final Integer tgChatId = Math.toIntExact(context.chat().id());
        final SendMessage message = userRepository.findOneByTgId(context.from().id())
                .map(user -> {
                    final List<Integer> vkChatIds = user.getChatsSettings().stream()
                            .filter(chatSettings -> Objects.equals(chatSettings.getTgChatId(), tgChatId))
                            .map(ChatSettings::getVkChatId)
                            .collect(toList());
                    final String code = getCodePrefix() + (vkChatIds.isEmpty() ? "no_content" : "info");
                    return new SendMessage(tgChatId, messages.getMessage(code))
                            .parseMode(ParseMode.Markdown)
                            .replyMarkup(createInlineKeyboardMarkup(user, vkChatIds, tgChatId));
                })
                .orElseGet(() -> new SendMessage(tgChatId, messages.getMessage(getCodePrefix() + "denied")));
        tgService.send(message);
    }

    private InlineKeyboardMarkup createInlineKeyboardMarkup(User user, List<Integer> vkChatIds, Integer tgChatId) {
        final List<Chat> chats = vkService.resolveChats(user);
        final InlineKeyboardButton[][] buttons = of(vkChatIds)
                .filter(ids -> !ids.isEmpty())
                .map(ids -> createButtonsRows(createButtonsList(chats, tgChatId, ids)))
                .orElse(new InlineKeyboardButton[0][0]);
        return new InlineKeyboardMarkup(buttons);
    }

    private InlineKeyboardButton[][] createButtonsRows(List<InlineKeyboardButton> buttons) {
        return partition(buttons, BUTTONS_COLUMNS_COUNT).stream()
                .map(partition -> partition.toArray(new InlineKeyboardButton[0]))
                .collect(toList())
                .toArray(new InlineKeyboardButton[0][0]);
    }

    private List<InlineKeyboardButton> createButtonsList(List<Chat> chats, Integer tgChatId, List<Integer> ids) {
        return chats.stream()
                .filter(chat -> ids.contains(chat.getId()))
                .map(chat -> createButton(tgChatId, chat))
                .collect(toList());
    }

    private InlineKeyboardButton createButton(Integer tgChatId, Chat chat) {
        final String json = gson.toJson(getCallbackData(tgChatId, chat));
        return new InlineKeyboardButton(chat.getTitle()).callbackData(json);
    }
}
