package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.ChatSettings;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.alebabai.tg2vk.util.TestUtils.*;

@Transactional
public class ChatSettingsRepositoryTest extends AbstractJpaRepositoryTest<ChatSettings, Integer, ChatSettingsRepository> {

    @Override
    protected ChatSettings generateEntity() {
        return generateChatSettings();
    }

    @Override
    protected List<? extends ChatSettings> generateEntities(int maxEntitiesCount) {
        return IntStream
                .rangeClosed(MIN_ENTITIES_COUNT, getRandomInteger(MAX_ENTITIES_COUNT))
                .parallel()
                .mapToObj(it -> new ChatSettings().setTgChatId(it).setVkChatId(it))
                .collect(Collectors.toList());
    }
}
