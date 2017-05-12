package com.github.alebabai.tg2vk.service.telegram.update.query.callback;

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
