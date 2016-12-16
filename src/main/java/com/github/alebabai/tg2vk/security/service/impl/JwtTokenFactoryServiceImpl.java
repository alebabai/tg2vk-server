package com.github.alebabai.tg2vk.security.service.impl;

import com.github.alebabai.tg2vk.domain.Role;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.security.config.JwtSettings;
import com.github.alebabai.tg2vk.security.service.JwtTokenFactoryService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JwtTokenFactoryServiceImpl implements JwtTokenFactoryService {

    private final JwtSettings settings;

    @Autowired
    public JwtTokenFactoryServiceImpl(JwtSettings settings) {
        this.settings = settings;
    }

    @Override
    public String create(User user) {
        Assert.notNull(user, "Can't generate JWT for null object");
        final Claims claims = Jwts.claims();
        claims.put("userId", user.getId());
        claims.put("tgId", user.getTgId());
        claims.put("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));

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
