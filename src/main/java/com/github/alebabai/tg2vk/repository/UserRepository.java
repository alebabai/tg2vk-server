package com.github.alebabai.tg2vk.repository;

import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
@RestResource(exported = false)
public interface UserRepository extends UserBaseRepository {
}
