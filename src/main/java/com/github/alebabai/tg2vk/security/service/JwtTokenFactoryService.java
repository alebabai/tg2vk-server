package com.github.alebabai.tg2vk.security.service;

import org.springframework.security.core.Authentication;

public interface JwtTokenFactoryService {

    String create(Authentication authentication);
}
