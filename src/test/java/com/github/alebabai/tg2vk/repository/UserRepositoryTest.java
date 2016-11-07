package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.User;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.alebabai.tg2vk.util.TestUtils.*;

@Transactional
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
                .mapToObj(it -> new User().setTgId(it).setVkId(it))
                .collect(Collectors.toList());
    }
}
