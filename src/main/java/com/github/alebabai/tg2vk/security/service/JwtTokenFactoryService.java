package com.github.alebabai.tg2vk.security.service;

import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public interface JwtTokenFactoryService {

    String create(PreAuthenticatedAuthenticationToken token);
}
