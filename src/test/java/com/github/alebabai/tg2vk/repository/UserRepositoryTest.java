package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.domain.Role;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.domain.UserSettings;
import org.junit.Assert;
import org.junit.Test;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.alebabai.tg2vk.util.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

@Transactional
public class UserRepositoryTest extends AbstractRepositoryTest<User, Integer, UserRepository> {

    @Override
    protected User generateEntity() {
        return generateUser();
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
    public void findAllStartedTest() {
        repository.save(generateEntity().setSettings(new UserSettings().setStarted(true)));
        repository.findAllStarted().forEach(user -> Assert.assertTrue(user.getSettings().isStarted()));
    }

    @Test
    public void findUserByTgIdTest() {
        final User user = repository.save(generateUser());

        assertThat(repository.findOneByTgId(user.getTgId()), is(Optional.of(user)));
    }

    @Test
    public void updateUserPropertiesTest() {
        final User user = repository.save(generateUser());

        user.setTgId(getRandomInteger(1000));
        user.setVkId(getRandomInteger(1000));
        user.setVkToken(getRandomString(1000));
        repository.save(user);

        final User updatedUser = repository.findOne(user.getId());

        assertThat(updatedUser, is(user));
        assertThat(updatedUser.getTgId(), is(user.getTgId()));
        assertThat(updatedUser.getVkId(), is(user.getVkId()));
        assertThat(updatedUser.getVkToken(), is(user.getVkToken()));
    }

    @Test
    public void updateUserAssociationsTest() {
        User user = repository.save(generateUser());

        final Integer tgChatId = getRandomInteger(1000);
        final Integer vkChatId = getRandomInteger(1000);
        final ChatSettings chatSettings = new ChatSettings(tgChatId, vkChatId);
        user.getChatsSettings().add(chatSettings);
        user.getSettings().setStarted(false);
        user.getRoles().add(Role.BANNED);
        user = repository.save(user);

        final User updatedUser = repository.findOne(user.getId());

        assertThat(updatedUser, is(user));
        assertThat(updatedUser.getSettings(), is(user.getSettings()));
        assertThat(updatedUser.getChatsSettings(), is(user.getChatsSettings()));
        assertThat(updatedUser.getRoles(), is(user.getRoles()));
    }


    @Test
    public void deleteUserAssociationsTest() {
        final User user = repository.save(generateUser());

        user.setChatsSettings(Collections.emptySet());
        user.setRoles(Collections.emptySet());
        repository.save(user);

        final User updatedUser = repository.findOne(user.getId());

        assertThat(updatedUser, is(user));
        assertThat(updatedUser.getChatsSettings(), empty());
        assertThat(updatedUser.getRoles(), empty());
    }
}
