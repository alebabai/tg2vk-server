package com.github.alebabai.tg2vk.service.tg.update.query.callback.handler.impl;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.tg.common.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

@Service("muteCallbackQueryHandler")
public class TelegramCallbackMuteChatsQueryHandler extends AbstractTelegramCallbackMuteChatQueryHandler {


    @Autowired
    public TelegramCallbackMuteChatsQueryHandler(UserRepository userRepository,
                                                 TelegramService tgService,
                                                 MessageSourceAccessor messages) {
        super(userRepository, tgService, messages);
    }

    @Override
    protected String getCodePrefix() {
        return "tg.callback.chats.mute.msg.";
    }

    @Override
    protected boolean getState() {
        return false;
    }

    @Override
    protected boolean isProcessable(ChatSettings chatSettings) {
        return chatSettings.isStarted();
    }

}
