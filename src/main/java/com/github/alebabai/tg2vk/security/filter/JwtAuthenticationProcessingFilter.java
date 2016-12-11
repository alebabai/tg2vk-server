package com.github.alebabai.tg2vk.security.filter;


import com.github.alebabai.tg2vk.security.config.JwtSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthenticationProcessingFilter extends RequestHeaderAuthenticationFilter {

    @Autowired
    public JwtAuthenticationProcessingFilter(JwtSettings settings) {
        super();
        this.setExceptionIfHeaderMissing(false);
        this.setPrincipalRequestHeader(settings.getHeaderName());
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}
