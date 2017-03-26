package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.domain.UserSettings;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.alebabai.tg2vk.util.TestUtils.*;

public class UserRepositoryTest extends AbstractJpaRepositoryTest<User, Integer, UserRepository> {

    @Override
    protected User generateEntity() {
        final ChatSettings chatSettings = generateChatSettings();
        final User user = generateUser();
        chatSettings.setUser(user);
        user.setChatsSettings(Collections.singleton(chatSettings));
        return user;
    }

    @Override
    protected List<? extends User> generateEntities(int maxEntitiesCount) {
        return IntStream
                .rangeClosed(MIN_ENTITIES_COUNT, getRandomInteger(MAX_ENTITIES_COUNT))
                .parallel()
                .mapToObj(it -> new User()
                        .setTgId(it)
                        .setVkId(it)
                        .setVkToken(getRandomString(MAX_STRING_LENGTH))
                        .setSettings(generateUserSettings()))
                .collect(Collectors.toList());
    }

    @Test
    public void findAllStarted() {
        repository.save(generateEntity().setSettings(new UserSettings().setStarted(true)));
        repository.findAllStarted().forEach(user -> Assert.assertTrue(user.getSettings().isStarted()));
    }
}
