package com.github.alebabai.tg2vk.service.telegram.update.command.handler.impl;

import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.telegram.common.TelegramService;
import com.github.alebabai.tg2vk.service.telegram.update.command.TelegramCommand;
import com.github.alebabai.tg2vk.service.telegram.update.command.handler.TelegramCommandHandler;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

@Service("link")
public class TelegramLinkCommandHandler implements TelegramCommandHandler {

    private final TelegramService tgService;
    private final UserRepository userRepository;
    private final MessageSourceAccessor messages;

    @Autowired
    public TelegramLinkCommandHandler(TelegramService tgService,
                                      UserRepository userRepository,
                                      MessageSourceAccessor messages) {
        this.tgService = tgService;
        this.userRepository = userRepository;
        this.messages = messages;
    }

    @Override
    public void handle(TelegramCommand command) {
        final Message context = command.context();
        final String query = StringUtils.join(command.args(), StringUtils.SPACE);
        final Long chatId = context.chat().id();
        final SendMessage message = userRepository.findOneByTgId(context.from().id())
                .map(user -> {
                    user.setTempTgChatId(Math.toIntExact(chatId));
                    userRepository.save(user);
                    return new SendMessage(chatId, messages.getMessage("tg.command.link.msg.info"))
                            .parseMode(ParseMode.Markdown)
                            .replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[]{
                                    new InlineKeyboardButton(messages.getMessage("tg.command.link.label.button"))
                                            .switchInlineQueryCurrentChat(query),
                            }));
                })
                .orElseGet(() -> new SendMessage(chatId, messages.getMessage("tg.command.link.msg.denied")));
        tgService.send(message);
    }
}
