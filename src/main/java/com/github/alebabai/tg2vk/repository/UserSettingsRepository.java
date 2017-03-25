package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "settings", collectionResourceRel = "settings", itemResourceRel = "settings")
public interface UserSettingsRepository extends JpaRepository<UserSettings, Integer> {
}
