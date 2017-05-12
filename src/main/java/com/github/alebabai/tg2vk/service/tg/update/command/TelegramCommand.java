package com.github.alebabai.tg2vk.service.tg.update.command;

import com.pengrad.telegrambot.model.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(fluent = true)
@RequiredArgsConstructor
public class TelegramCommand {
    private final String name;
    private final List<String> args;
    private final Message context;
}
