package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.ChatSettings;
import com.github.alebabai.tg2vk.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(path = "chat-settings", collectionResourceRel = "chat-settings", itemResourceRel = "chat-settings")
public interface ChatSettingsRepository extends JpaRepository<ChatSettings, Integer> {
    Optional<ChatSettings> findOneByUserAndVkChatId(User user, Integer vkChatId);
}
