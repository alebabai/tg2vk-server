package com.github.alebabai.tg2vk.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtSettings {

    @Value("${jwt.token.headerName}")
    private String headerName;

    @Value("${jwt.token.signKey}")
    private String signKey;

    @Value("${jwt.token.expirationTime}")
    private long expirationTime;

    public String getHeaderName() {
        return headerName;
    }

    public String getSignKey() {
        return signKey;
    }

    public long getExpirationTime() {
        return expirationTime;
    }
}
