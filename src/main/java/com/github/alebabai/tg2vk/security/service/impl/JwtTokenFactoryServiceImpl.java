package com.github.alebabai.tg2vk.security.service.impl;

import com.github.alebabai.tg2vk.security.config.JwtSettings;
import com.github.alebabai.tg2vk.security.service.JwtTokenFactoryService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class JwtTokenFactoryServiceImpl implements JwtTokenFactoryService {

    private final JwtSettings settings;

    @Autowired
    public JwtTokenFactoryServiceImpl(JwtSettings settings) {
        this.settings = settings;
    }

    @Override
    public String create(Authentication authentication) {
        Assert.notNull(authentication, "Can't generate JWT for empty object");
        //TODO create token for User entity (user will have roles, etc...)
        final Claims claims = Jwts.claims();
        claims.put("userId", authentication.getPrincipal());
        claims.put("tgId", authentication.getCredentials());
        claims.put("authorities", authentication.getAuthorities());

        final LocalDateTime currentTime = LocalDateTime.now();
        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Timestamp.valueOf(currentTime))
                .setExpiration(Timestamp.valueOf(currentTime.plusMinutes(settings.getExpirationTime())))
                .signWith(SignatureAlgorithm.HS512, settings.getSignKey())
                .compact();
    }
}
