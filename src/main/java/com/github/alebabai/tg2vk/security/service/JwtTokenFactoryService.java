package com.github.alebabai.tg2vk.security.service;

public interface JwtTokenFactoryService {
    String create(Integer tgId, String... roles);
}
