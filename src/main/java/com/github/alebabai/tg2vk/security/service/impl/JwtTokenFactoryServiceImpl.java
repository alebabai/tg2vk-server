package com.github.alebabai.tg2vk.security.service.impl;

import com.github.alebabai.tg2vk.domain.Role;
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
import java.util.stream.Stream;

@Service
public class JwtTokenFactoryServiceImpl implements JwtTokenFactoryService {

    private final JwtSettings settings;

    @Autowired
    public JwtTokenFactoryServiceImpl(JwtSettings settings) {
        this.settings = settings;
    }

    @Override
    public String generate(Integer tgId, Role... roles) {
        Assert.notNull(tgId, "Can't generate JWT for null tgId");
        Assert.noNullElements(roles, "Can't generate JWT without roles");

        final Claims claims = Jwts.claims();
        claims.put("tgId", tgId);
        claims.put("roles", Stream.of(roles).map(Role::getName).collect(Collectors.toList()));

        final LocalDateTime currentTime = LocalDateTime.now();
        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Timestamp.valueOf(currentTime))
                .setExpiration(Timestamp.valueOf(currentTime.plus(settings.getExpirationTime(), settings.getTimeUnit())))
                .signWith(SignatureAlgorithm.HS512, settings.getSignKey())
                .compact();
    }
}
