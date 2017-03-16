package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.domain.UserSettings;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.alebabai.tg2vk.util.TestUtils.*;

public class UserRepositoryTest extends AbstractJpaRepositoryTest<User, Integer, UserRepository> {

    @Override
    protected User generateEntity() {
        return generateUser();
    }

    @Override
    protected List<? extends User> generateEntities(int maxEntitiesCount) {
        return IntStream
                .rangeClosed(MIN_ENTITIES_COUNT, getRandomInteger(MAX_ENTITIES_COUNT))
                .parallel()
                .mapToObj(it -> new User().setTgId(it).setVkId(it).setVkToken(getRandomString(MAX_STRING_LENGTH)))
                .collect(Collectors.toList());
    }

    @Test
    public void findAllStarted() {
        repository.save(generateEntity().setSettings(new UserSettings().started(true)));
        repository.findAllStarted().forEach(user -> Assert.assertTrue(user.getSettings().isStarted()));
    }
}
