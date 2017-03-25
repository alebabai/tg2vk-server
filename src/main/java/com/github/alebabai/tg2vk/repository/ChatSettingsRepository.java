package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "chat-settings", collectionResourceRel = "chat-settings", itemResourceRel = "chat-settings")
public interface ChatSettingsRepository extends JpaRepository<ChatSettings, Integer> {
}
