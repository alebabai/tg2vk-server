package com.github.alebabai.tg2vk.util;

import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.users.User;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.beans.propertyeditors.ResourceBundleEditor.BASE_NAME_SEPARATOR;

public final class Tg2vkMapperUtils {

    private Tg2vkMapperUtils() {
        super();
    }

    public static Map<String, Object> createPrivateMessageContext(User profile, Message message) {
        final Map<String, Object> context = new HashMap<>();
        context.put("user", String.join(StringUtils.SPACE, profile.getFirstName(), profile.getLastName()));
        context.put("status", profile.isOnline() ? "online" : "offline");
        context.put("body", message.getBody());
        return context;
    }

    public static Map<String, Object> createGroupMessageContext(User profile, Message message) {
        final Map<String, Object> context = createPrivateMessageContext(profile, message);
        context.put("chat", createValidHashTag(message.getTitle()));
        context.put("online_count", Collections.size(message.getChatActive()));
        return context;
    }

    public static String createValidHashTag(String title) {
        return "#" + StringUtils.replacePattern(title, "[^\\p{L}\\d]+", BASE_NAME_SEPARATOR);
    }

    public static Integer getVkChatId(Message message) {
        return Optional.ofNullable(message.getChatId()).orElse(message.getUserId());
    }
}
