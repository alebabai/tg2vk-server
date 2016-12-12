package com.github.alebabai.tg2vk.security.provider;

import com.github.alebabai.tg2vk.security.config.JwtSettings;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

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
            final Integer userId = jws.getBody().get("userId", Integer.TYPE);//TODO check if user exist
            final Integer tgId = jws.getBody().get("tgId", Integer.TYPE);//TODO check if user has the same tgId
            final List<GrantedAuthority> authorities = jws.getBody().get("authorities", List.class);//TODO check if user has the same roles
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
        return new AnonymousAuthenticationToken((String) authentication.getPrincipal(), "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
