package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.PathResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PathResolverImpl implements PathResolver {

    @Value("${tg2vk.client.url}")
    private String clientBaseUrl;

    @Value("${tg2vk.server.lb.scheme:${tg2vk.server.scheme:http}}")
    private String serverScheme;

    @Value("${tg2vk.server.lb.name:${tg2vk.server.name:localhost}}")
    private String serverName;

    @Value("${tg2vk.server.lb.port:${tg2vk.server.port:80}}")
    private Integer serverPort;

    @Override
    public String getClientUrl() {
        return clientBaseUrl;
    }

    @Override
    public String getServerUrl() {
        return UriComponentsBuilder.newInstance()
                .scheme(serverScheme)
                .host(serverName)
                .port(serverPort)
                .toUriString();
    }

    @Override
    public String resolveServerUrl(String relativePath) {
        return UriComponentsBuilder.newInstance()
                .scheme(serverScheme)
                .host(serverName)
                .port(serverPort)
                .path(relativePath)
                .toUriString();
    }

    @Override
    public String resolveClientUrl(String relativePath) {
        return UriComponentsBuilder.fromUriString(clientBaseUrl)
                .replacePath(relativePath)
                .toUriString();
    }
}
