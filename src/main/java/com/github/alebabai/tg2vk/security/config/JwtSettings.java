package com.github.alebabai.tg2vk.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;

@Component
public class JwtSettings {

    @Value("${tg2vk.security.jwt.header_name}")
    private String headerName;

    @Value("${tg2vk.security.jwt.sign_key}")
    private String signKey;

    @Value("${tg2vk.security.jwt.expiration_time}")
    private long expirationTime;

    @Value("${tg2vk.security.jwt.expiration_time_unit}")
    private String timeUnitName;

    private TemporalUnit timeUnit;

    @PostConstruct
    protected void init() {
        this.timeUnit = Arrays.stream(ChronoUnit.values())
                .filter(it -> it.name().equals(timeUnitName))
                .findAny()
                .orElse(ChronoUnit.MINUTES);
    }

    public String getHeaderName() {
        return headerName;
    }

    public String getSignKey() {
        return signKey;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public TemporalUnit getTimeUnit() {
        return timeUnit;
    }
}
