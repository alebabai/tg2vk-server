package com.github.alebabai.tg2vk.service.tg.update.impl;

import com.github.alebabai.tg2vk.service.tg.update.TelegramUpdateHandler;
import com.github.alebabai.tg2vk.service.tg.update.command.TelegramCommand;
import com.github.alebabai.tg2vk.service.tg.update.command.TelegramCommandProcessor;
import com.github.alebabai.tg2vk.service.tg.update.query.callback.TelegramCallbackQueryProcessor;
import com.github.alebabai.tg2vk.service.tg.update.query.inline.TelegramInlineQueryProcessor;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.github.alebabai.tg2vk.util.CommandUtils.parseCommand;

@Service
public class TelegramUpdateHandlerImpl implements TelegramUpdateHandler {

    private final TelegramCommandProcessor commandProcessor;
    private final TelegramCallbackQueryProcessor callbackQueryProcessor;
    private final TelegramInlineQueryProcessor inlineQueryProcessor;

    @Autowired
    public TelegramUpdateHandlerImpl(TelegramCommandProcessor commandProcessor,
                                     TelegramCallbackQueryProcessor callbackQueryProcessor,
                                     TelegramInlineQueryProcessor inlineQueryProcessor) {
        this.commandProcessor = commandProcessor;
        this.callbackQueryProcessor = callbackQueryProcessor;
        this.inlineQueryProcessor = inlineQueryProcessor;
    }

    @Override
    public void onInlineQueryReceived(InlineQuery query) {
        inlineQueryProcessor.process(query);
    }

    @Override
    public void onCallbackQueryReceived(CallbackQuery callbackQuery) {
        callbackQueryProcessor.process(callbackQuery);
    }

    @Override
    public void onMessageReceived(Message message) {
        if (message.text().startsWith("/")) {
            parseCommand(message.text(), (command, args) -> commandProcessor.process(new TelegramCommand(command, args, message)));
        }
    }
}
