package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.UserSettings;

import javax.transaction.Transactional;

import static com.github.alebabai.tg2vk.util.TestUtils.generateUserSettings;

@Transactional
public class UserSettingsRepositoryTest extends AbstractJpaRepositoryTest<UserSettings, Integer, UserSettingsRepository> {

    @Override
    protected UserSettings generateEntity() {
        return generateUserSettings();
    }
}
