package com.github.alebabai.tg2vk.service.core.impl;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.repository.UserRepository;
import com.github.alebabai.tg2vk.service.core.LinkerService;
import com.github.alebabai.tg2vk.service.tg.core.TelegramService;
import com.github.alebabai.tg2vk.service.core.TemplateRenderer;
import com.github.alebabai.tg2vk.util.Tg2vkMapperUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.vk.api.sdk.objects.audio.AudioFull;
import com.vk.api.sdk.objects.docs.Doc;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.MessageAttachment;
import com.vk.api.sdk.objects.users.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class LinkerServiceImpl implements LinkerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkerServiceImpl.class);
    private static final String PRIVATE_MESSAGE_TEMPLATE = "telegram/private_message.html";
    private static final String GROUP_MESSAGE_TEMPLATE = "telegram/group_message.html";

    private final UserRepository userRepository;
    private final TelegramService tgService;
    private final TemplateRenderer templateRenderer;
    private final MessageSourceAccessor messages;
    private final OkHttpClient httpClient;
    private final Gson gson;

    @Autowired
    public LinkerServiceImpl(UserRepository userRepository,
                             TelegramService tgService,
                             TemplateRenderer templateRenderer,
                             MessageSource messageSource) {
        this.userRepository = userRepository;
        this.tgService = tgService;
        this.templateRenderer = templateRenderer;
        this.messages = new MessageSourceAccessor(messageSource);
        this.httpClient = new OkHttpClient.Builder().build();
        this.gson = new GsonBuilder().create();
    }

    @Override
    public BiConsumer<User, Message> getVkMessageHandler(Integer userId) {
        return (profile, message) -> {
            try {
                final Integer vkChatId = Tg2vkMapperUtils.getVkChatId(message);
                Optional.ofNullable(userRepository.findOne(userId))
                        .map(user -> Optional.of(user.getChatsSettings().stream()
                                .filter(chatSettings -> Objects.equals(chatSettings.getVkChatId(), vkChatId))
                                .collect(toList()))
                                .filter(chatSettings -> !chatSettings.isEmpty())
                                .orElseGet(() -> Collections.singletonList(new ChatSettings()
                                        .setTgChatId(user.getTgId())
                                        .setVkChatId(vkChatId)
                                        .setStarted(true))))
                        .filter(chatSettings -> chatSettings.stream().allMatch(ChatSettings::isStarted))
                        .map(chatSettings -> chatSettings.stream().map(ChatSettings::getTgChatId))
                        .ifPresent(chatSettingsStream -> chatSettingsStream.forEach(getMainHandler(message, profile)));
            } catch (Exception e) {
                LOGGER.error("Error during vk message handling: ", e);
            }
        };
    }

    private Consumer<Integer> getMainHandler(Message message, com.vk.api.sdk.objects.users.User profile) {
        return tgChatId -> Optional.ofNullable(message.getFwdMessages())
                .map(fwdMessages -> fwdMessages.stream()
                        .map(fwdMessage -> mapFwdMessage(message, fwdMessage)))
                .orElse(Stream.of(message))
                .forEach(msg -> tgService.send(convertMessage(tgChatId, msg, profile)));
    }

    private Message mapFwdMessage(Message origin, Message target) {
        try {
            ReflectionUtils.setField(Message.class.getDeclaredField("chatId"), target, origin.getChatId());
            ReflectionUtils.setField(Message.class.getDeclaredField("title"), target, origin.getTitle());
            ReflectionUtils.setField(Message.class.getDeclaredField("chatActive"), target, origin.getChatActive());
        } catch (NoSuchFieldException e) {
            LOGGER.error("Can't patch forwarded message object: ", e);
        }
        return target;
    }

    private AbstractSendRequest convertMessage(Integer tgChatId, Message message, com.vk.api.sdk.objects.users.User profile) {
        return Optional.ofNullable(message.getAttachments())
                .map(attachments -> attachments.get(0))
                .map(attachment -> convertMessageAttachment(tgChatId, message, attachment, profile))
                .orElse(Optional.ofNullable(message.getGeo())
                        .map(geo -> {
                            final String[] coordinates = StringUtils.split(geo.getCoordinates(), StringUtils.SPACE);
                            float latitude = NumberUtils.toFloat(coordinates[0]);
                            float longitude = NumberUtils.toFloat(coordinates[1]);
                            return (AbstractSendRequest) new SendLocation(tgChatId, latitude, longitude);
                        })
                        .orElse(convertTextMessage(tgChatId, message, profile))
                );
    }

    private AbstractSendRequest convertMessageAttachment(Integer tgChatId, Message message, MessageAttachment attachment, com.vk.api.sdk.objects.users.User profile) {
        AbstractSendRequest result;
        switch (attachment.getType()) {
            case PHOTO:
                final Message photoMessage = createFakeVkMessage(message, attachment.getPhoto().getPhoto604());
                result = convertTextMessage(tgChatId, photoMessage, profile);
                break;
            case AUDIO:
                final AudioFull audio = attachment.getAudio();
                result = new SendAudio(tgChatId, fetchAttachment(attachment.getAudio().getUrl()))
                        .caption(message.getBody())
                        .duration(audio.getDuration())
                        .performer(audio.getArtist())
                        .title(audio.getTitle());
                break;
            case VIDEO:
                final String videoText = messages.getMessage("vk.messages.attachment.video", StringUtils.EMPTY) + attachment.getVideo().getPhoto320();
                final Message videoMessage = createFakeVkMessage(message, videoText);
                result = convertTextMessage(tgChatId, videoMessage, profile);
                break;
            case DOC:
                final Doc doc = attachment.getDoc();
                result = new SendDocument(tgChatId, fetchAttachment(doc.getUrl()))
                        .caption(message.getBody())
                        .fileName(doc.getTitle());
                break;
            case LINK:
                final Message linkMessage = createFakeVkMessage(message, attachment.getLink().getUrl());
                result = convertTextMessage(tgChatId, linkMessage, profile);
                break;
            case GIFT:
                final String giftText = messages.getMessage("vk.messages.attachment.gift", StringUtils.EMPTY) + attachment.getGift().getThumb256();
                final Message giftMessage = createFakeVkMessage(message, giftText);
                result = convertTextMessage(tgChatId, giftMessage, profile);
                break;
            case STICKER:
                final String stickerText = messages.getMessage("vk.messages.attachment.sticker", StringUtils.EMPTY) + attachment.getSticker().getPhoto256();
                final Message stickerMessage = createFakeVkMessage(message, stickerText);
                result = convertTextMessage(tgChatId, stickerMessage, profile);
                break;
            case WALL:
                final String wallText = messages.getMessage("vk.messages.attachment.wall", StringUtils.EMPTY) + attachment.getWall().getText();
                final Message wallMessage = createFakeVkMessage(message, wallText);
                result = convertTextMessage(tgChatId, wallMessage, profile);
                break;
            case WALL_REPLY:
                final String wallReplyText = messages.getMessage("vk.messages.attachment.wall_reply", StringUtils.EMPTY) + attachment.getWallReply().getText();
                final Message wallReplyMessage = createFakeVkMessage(message, wallReplyText);
                result = convertTextMessage(tgChatId, wallReplyMessage, profile);
                break;
            default:
                result = convertTextMessage(tgChatId, message, profile);
                break;
        }
        return result;
    }

    private Message createFakeVkMessage(Message origin, String body) {
        final JsonObject jsonObject = gson.toJsonTree(origin).getAsJsonObject();
        jsonObject.addProperty("body", body);
        return gson.fromJson(jsonObject, Message.class);
    }

    private byte[] fetchAttachment(String url) {
        try {
            return httpClient.newCall(new Request.Builder().url(url).build()).execute().body().bytes();
        } catch (IOException e) {
            LOGGER.debug("Can't fetch message attachment from url {}: {}", url, e);
        }
        return new byte[0];
    }

    private SendMessage convertTextMessage(Integer tgChatId, Message message, com.vk.api.sdk.objects.users.User profile) {
        return Optional.ofNullable(message.getChatId())
                .map(it -> createTelegramMessage(tgChatId, GROUP_MESSAGE_TEMPLATE, Tg2vkMapperUtils.createGroupMessageContext(profile, message)))
                .orElse(createTelegramMessage(tgChatId, PRIVATE_MESSAGE_TEMPLATE, Tg2vkMapperUtils.createPrivateMessageContext(profile, message)));
    }

    private SendMessage createTelegramMessage(Object tgChatId, String templateName, Map<String, Object> context) {
        return new SendMessage(tgChatId, templateRenderer.render(templateName, context))
                .parseMode(ParseMode.HTML);
    }
}
