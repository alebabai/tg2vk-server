package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.domain.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.alebabai.tg2vk.util.TestUtils.*;

public class ChatSettingsRepositoryTest extends AbstractJpaRepositoryTest<ChatSettings, Integer, ChatSettingsRepository> {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected ChatSettings generateEntity() {
        final User user = userRepository.save(generateUser());
        return generateChatSettings().setUser(user);
    }

    @Override
    protected List<? extends ChatSettings> generateEntities(int maxEntitiesCount) {
        final User user = userRepository.save(generateUser());
        return IntStream
                .rangeClosed(MIN_ENTITIES_COUNT, getRandomInteger(MAX_ENTITIES_COUNT))
                .parallel()
                .mapToObj(it -> new ChatSettings().setTgChatId(it).setVkChatId(it).setUser(user))
                .collect(Collectors.toList());
    }
}
