package com.github.alebabai.tg2vk.security.service.impl;

import com.github.alebabai.tg2vk.security.config.JwtSettings;
import com.github.alebabai.tg2vk.security.service.JwtTokenFactoryService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

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
    public String create(PreAuthenticatedAuthenticationToken token) {
        if (token == null) {
            throw new IllegalArgumentException("Can't generate JWT for empty object");
        }

        final Claims claims = Jwts.claims();
        claims.put("userId", token.getPrincipal());
        claims.put("tgId", token.getCredentials());
        claims.put("authorities", token.getAuthorities());

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
