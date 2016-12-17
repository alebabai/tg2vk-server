package com.github.alebabai.tg2vk.repository;

import com.github.alebabai.tg2vk.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findOneByName(String name);
}
