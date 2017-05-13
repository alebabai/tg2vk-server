package com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.impl;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.tg.common.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

@Service("unmuteCallbackQueryHandler")
public class TelegramCallbackUnmuteChatsQueryHandler extends AbstractTelegramCallbackMuteChatQueryHandler {


    @Autowired
    public TelegramCallbackUnmuteChatsQueryHandler(UserRepository userRepository,
                                                   TelegramService tgService,
                                                   MessageSourceAccessor messages) {
        super(userRepository, tgService, messages);
    }

    @Override
    protected String getCodePrefix() {
        return "tg.callback.chats.unmute.msg.";
    }

    @Override
    protected boolean getState() {
        return true;
    }

    @Override
    protected boolean isProcessable(ChatSettings chatSettings) {
        return !chatSettings.isStarted();
    }
}
