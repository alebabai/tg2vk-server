package com.github.alebabai.tg2vk.security.config;

import com.github.alebabai.tg2vk.security.provider.JwtAuthenticationProvider;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.servlet.Filter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationProvider authenticationProvider;
    private final JwtSettings settings;

    @Autowired
    public WebSecurityConfig(JwtAuthenticationProvider authenticationProvider, JwtSettings settings) throws Exception {
        this.authenticationProvider = authenticationProvider;
        this.settings = settings;
    }

    @Bean
    protected Filter jwtFilter() throws Exception {
        final RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setExceptionIfHeaderMissing(false);
        filter.setPrincipalRequestHeader(settings.getHeaderName());
        filter.setAuthenticationManager(this.authenticationManager());
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .and()
                .authorizeRequests()
                .mvcMatchers(PathConstants.API_TELEGRAM_FETCH_UPDATES).permitAll()
                .mvcMatchers(PathConstants.API + "/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .and()
                .authenticationProvider(authenticationProvider)
                .sessionManagement().disable()
                .addFilterBefore(jwtFilter(), RequestHeaderAuthenticationFilter.class);
    }
}
