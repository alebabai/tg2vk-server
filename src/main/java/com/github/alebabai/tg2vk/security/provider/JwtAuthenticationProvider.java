package com.github.alebabai.tg2vk.security.provider;

import com.github.alebabai.tg2vk.security.config.JwtSettings;
import com.github.alebabai.tg2vk.util.constants.SecurityConstants;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

    private final JwtParser parser;

    @Autowired
    public JwtAuthenticationProvider(JwtSettings settings) {
        this.parser = Jwts.parser().setSigningKey(settings.getSignKey());
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        try {
            final String rawToken = (String) authentication.getPrincipal();
            final Jws<Claims> jws = parser.parseClaimsJws(rawToken);//TODO check if signed or try catch
            final Object userId = jws.getBody().get("userId");//TODO check if user exist
            final Object tgId = jws.getBody().get("tgId");//TODO check if user has the same tgId
            final List<String> roles = jws.getBody().get("roles", List.class);//TODO check if user has the same roles
            final List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            return new PreAuthenticatedAuthenticationToken(userId, tgId, authorities);//TODO throw AuthenticationException if some condition has false value
        } catch (RequiredTypeException | SignatureException | UnsupportedJwtException e) {
            LOGGER.debug("Incorrect token format: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.debug("Attempt to use expired token: {}", e.getMessage());
        } catch (AuthenticationException e) {
            LOGGER.debug("Authentication failed: {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Something wrong in authentication process: ", e);
        }
        return new AnonymousAuthenticationToken((String) authentication.getPrincipal(), "anonymous", AuthorityUtils.createAuthorityList(SecurityConstants.ROLE_ANONYMOUS));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
