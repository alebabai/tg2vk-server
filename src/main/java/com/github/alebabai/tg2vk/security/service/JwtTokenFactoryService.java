package com.github.alebabai.tg2vk.security.service;

import com.github.alebabai.tg2vk.domain.Role;

public interface JwtTokenFactoryService {
    String generate(Integer tgId, Role... roles);
}
