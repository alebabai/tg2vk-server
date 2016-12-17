package com.github.alebabai.tg2vk.security.service;

public interface JwtTokenFactoryService {
    String generate(Integer tgId, String... roles);
}
