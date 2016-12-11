package com.github.alebabai.tg2vk.security.provider;

import com.github.alebabai.tg2vk.security.config.JwtSettings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtParser parser;

    @Autowired
    public JwtAuthenticationProvider(JwtSettings settings) {
        this.parser = Jwts.parser().setSigningKey(settings.getSignKey());
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        final String rawToken = (String) authentication.getPrincipal();
        final Jws<Claims> jws = parser.parseClaimsJws(rawToken);
        final Integer userId = jws.getBody().get("userId", Integer.TYPE);//TODO check if user exist
        final Integer tgId = jws.getBody().get("tgId", Integer.TYPE);//TODO check if user has the same tgId
        final List<GrantedAuthority> authorities = jws.getBody().get("authorities", List.class);
        return new PreAuthenticatedAuthenticationToken(userId, tgId, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return AbstractAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
