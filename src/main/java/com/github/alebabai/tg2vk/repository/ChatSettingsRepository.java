package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSettingsRepository extends JpaRepository<ChatSettings, Integer> {
}
