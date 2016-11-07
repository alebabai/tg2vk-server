package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Integer> {
}
