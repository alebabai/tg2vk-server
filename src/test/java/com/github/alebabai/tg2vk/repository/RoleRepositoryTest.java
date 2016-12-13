package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.Role;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.domain.UserSettings;
import org.junit.Assert;
import org.junit.Test;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.alebabai.tg2vk.util.TestUtils.*;

@Transactional
public class RoleRepositoryTest extends AbstractJpaRepositoryTest<Role, Integer, RoleRepository> {

    @Override
    protected Role generateEntity() {
        return generateRole();
    }

    @Override
    protected List<? extends Role> generateEntities(int maxEntitiesCount) {
        return IntStream
                .rangeClosed(MIN_ENTITIES_COUNT, getRandomInteger(MAX_ENTITIES_COUNT))
                .parallel()
                .mapToObj(it -> new Role().setName(getRandomString(MAX_STRING_LENGTH)))
                .collect(Collectors.toList());
    }
}
