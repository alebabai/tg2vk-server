package com.github.alebabai.tg2vk.util;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.domain.Role;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.domain.UserSettings;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.assertj.core.util.Sets;
import org.junit.Assert;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.dao.support.DataAccessUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

public abstract class TestUtils {

    public static final int MIN_ENTITIES_COUNT = 1;
    public static final int MAX_STRING_LENGTH = 25;
    public static final int MAX_ENTITIES_COUNT = 20;

    public static String getRandomString(int length) {
        return RandomStringUtils.random(length, true, true);
    }

    public static Integer getRandomInteger(int bound) {
        return RandomUtils.nextInt(1, bound);
    }

    public static Boolean getRandomBoolean() {
        return BooleanUtils.toBoolean(getRandomInteger(1));
    }

    public static Long getRandomLong(long bound) {
        return RandomUtils.nextLong(1, bound);
    }

    public static User generateUser() {
        return new User()
                .setTgId(getRandomInteger(Integer.MAX_VALUE))
                .setVkId(getRandomInteger(Integer.MAX_VALUE))
                .setVkToken(getRandomString(MAX_STRING_LENGTH))
                .setRoles(Collections.singleton(Role.USER))
                .setSettings(generateUserSettings())
                .setChatsSettings(Collections.singleton(generateChatSettings()));
    }

    public static UserSettings generateUserSettings() {
        return new UserSettings().setStarted(getRandomBoolean());
    }

    public static ChatSettings generateChatSettings() {
        return new ChatSettings()
                .setTgChatId(getRandomInteger(Integer.MAX_VALUE))
                .setVkChatId(getRandomInteger(Integer.MAX_VALUE))
                .setAnswerAllowed(getRandomBoolean())
                .setStarted(getRandomBoolean());
    }

    public static Role generateRole() {
        return Stream.of(Role.values()).findAny().orElse(Role.ANONYMOUS);
    }
}
