package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface UserBaseRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u  WHERE u.settings.id IN (SELECT s.id FROM UserSettings s WHERE s.started=true)")
    List<User> findAllStarted();

    Optional<User> findOneByVkId(@Param("vkId") Integer id);

    Optional<User> findOneByTgId(@Param("tgId") Integer id);
}
