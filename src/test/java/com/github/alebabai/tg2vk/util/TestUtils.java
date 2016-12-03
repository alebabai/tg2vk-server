package com.github.alebabai.tg2vk.util;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.domain.UserSettings;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public abstract class TestUtils {

    public static final int MIN_ENTITIES_COUNT = 1;
    public static final int MAX_STRING_LENGTH = 25;
    public static final int MAX_NUMBER = 10000;
    public static final int MAX_ENTITIES_COUNT = 20;

    public static String getRandomString(int length) {
        return RandomStringUtils.random(length, true, true);
    }

    public static Integer getRandomInteger(int bound) {
        return RandomUtils.nextInt(1, bound);
    }

    public static Long getRandomLong(long bound) {
        return RandomUtils.nextLong(1, bound);
    }

    public static User generateUser() {
        return new User()
                .setTgId(getRandomInteger(MAX_NUMBER))
                .setVkId(getRandomInteger(MAX_NUMBER))
                .setVkToken(getRandomString(MAX_STRING_LENGTH));
    }

    public static UserSettings generateUserSettings() {
        return new UserSettings();
    }

    public static ChatSettings generateChatSettings() {
        return new ChatSettings()
                .setTgChatId(getRandomInteger(MAX_NUMBER))
                .setVkChatId(getRandomInteger(MAX_NUMBER))
                .answerAllowed(false);
    }
}
