package com.github.alebabai.tg2vk.service.tg.update.query.callback.handler;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@Builder
public class TelegramCallbackQueryData {
    private String type;
    private Integer tgChatId;
    private Integer vkChatId;
}
