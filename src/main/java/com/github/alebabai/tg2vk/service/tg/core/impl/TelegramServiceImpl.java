package com.github.alebabai.tg2vk.service.tg.core.impl;

import com.github.alebabai.tg2vk.domain.Chat;
import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.domain.ChatType;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.service.core.PathResolver;
import com.github.alebabai.tg2vk.service.tg.core.TelegramService;
import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.DeleteWebhook;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.github.alebabai.tg2vk.domain.ChatType.GROUP_CHAT;
import static com.github.alebabai.tg2vk.domain.ChatType.PRIVATE_CHAT;
import static com.pengrad.telegrambot.model.Chat.Type.Private;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.*;

@Service
public class TelegramServiceImpl implements TelegramService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramServiceImpl.class);

    @Value("${tg2vk.telegram.bot.max_connections:40}")
    private Integer maxConnectionsCount;

    private final TelegramBot bot;
    private final PathResolver pathResolver;

    @Autowired
    public TelegramServiceImpl(PathResolver pathResolver, @Value("${tg2vk.telegram.bot.token}") String token) {
        this.bot = TelegramBotAdapter.build(token);
        this.pathResolver = pathResolver;
        this.maxConnectionsCount = 40;
    }

    @Override
    public void startLongPollingUpdates(Consumer<Update> callback) {
        bot.setUpdatesListener(updates -> {
            updates.forEach(callback);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    @Override
    public void stopLongPollingUpdates() {
        bot.removeGetUpdatesListener();
    }

    @PostConstruct
    @Override
    public void startWebHookUpdates() {
        try {
            final SetWebhook request = new SetWebhook()
                    .url(pathResolver.resolveServerUrl("/api/telegram/updates"))
                    .maxConnections(maxConnectionsCount);
            bot.execute(request, loggerCallback());
        } catch (Exception e) {
            LOGGER.error("Error during webhook setup: ", e);
        }
    }

    @PreDestroy
    @Override
    public void stopWebHookUpdates() {
        try {
            DeleteWebhook request = new DeleteWebhook();
            bot.execute(request, loggerCallback());
        } catch (Exception e) {
            LOGGER.error("Error during webhook disabling: ", e);
        }
    }

    @Override
    public void send(BaseRequest request) {
        bot.execute(request, loggerCallback());
    }

    @Override
    public List<Chat> resolveChats(User user) {
        return user.getChatsSettings().parallelStream()
                .map(ChatSettings::getTgChatId)
                .map(id -> ofNullable(bot.execute(new GetChat(id)))
                        .map(GetChatResponse::chat)
                        .map(chat -> {
                            final String title = defaultIfBlank(chat.title(),
                                    defaultIfBlank(chat.username(),
                                            String.join(SPACE, defaultString(chat.firstName()), defaultString(chat.lastName()))));
                            final ChatType chatType = Objects.equals(chat.type(), Private) ? PRIVATE_CHAT : GROUP_CHAT;
                            return new Chat(id, title, chatType);
                        })
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private <T extends BaseRequest<T, R>, R extends BaseResponse> Callback<T, R> loggerCallback() {
        return new Callback<T, R>() {
            @Override
            public void onResponse(T request, R response) {
                LOGGER.trace("Successfully processed {}", request);
                LOGGER.trace("Telegram server response {}", response);
            }

            @Override
            public void onFailure(T request, IOException e) {
                LOGGER.error("Error during {} execution!", request);
                LOGGER.error(e.getMessage(), e);
            }
        };
    }
}
