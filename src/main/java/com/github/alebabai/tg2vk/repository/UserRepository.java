package com.github.alebabai.tg2vk.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
@RepositoryRestResource(exported = false)
public interface UserRepository extends UserBaseRepository {
}
