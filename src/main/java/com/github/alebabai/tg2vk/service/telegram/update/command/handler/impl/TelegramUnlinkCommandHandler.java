package com.github.alebabai.tg2vk.service.telegram.update.command.handler.impl;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.telegram.common.TelegramService;
import com.github.alebabai.tg2vk.service.telegram.update.command.TelegramCommand;
import com.github.alebabai.tg2vk.service.vk.VkService;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;

@Service("unlink")
public class TelegramUnlinkCommandHandler extends AbstractTelegramCommandHandler {

    private final UserRepository userRepository;
    private final VkService vkService;

    public TelegramUnlinkCommandHandler(TelegramService tgService, VkService vkService, UserRepository userRepository, MessageSource messageSource) {
        super(tgService, messageSource);
        this.userRepository = userRepository;
        this.vkService = vkService;
    }


    @Override
    public void handle(TelegramCommand command) {
        final Message context = command.context();
        final Integer tgChatId = Math.toIntExact(context.chat().id());
        final SendMessage message = userRepository.findOneByTgId(context.from().id())
                .map(user -> {
                    user.setTempTgChatId(Math.toIntExact(tgChatId));
                    final List<Integer> vkChatIds = user.getChatsSettings().parallelStream()
                            .filter(chatSettings -> Objects.equals(chatSettings.getTgChatId(), tgChatId))
                            .map(ChatSettings::getVkChatId)
                            .collect(toList());
                    final InlineKeyboardButton[] buttons = of(vkChatIds)
                            .filter(ids -> !ids.isEmpty())
                            .map(ids -> vkService.resolveChats(user).parallelStream()
                                    .filter(chat -> ids.contains(chat.getId()))
                                    .map(chat -> {
                                        final String data = String.join("|", "unlink", chat.getId().toString(), tgChatId.toString());
                                        return new InlineKeyboardButton(chat.getTitle()).callbackData(data);
                                    })
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
}
