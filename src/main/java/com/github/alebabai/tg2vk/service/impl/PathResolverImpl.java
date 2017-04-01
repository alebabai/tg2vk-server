package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.PathResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Service;

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
        return UrlUtils.buildFullRequestUrl(serverScheme, serverName, serverPort, null, null);
    }

    @Override
    public String getAbsoluteUrl(String relativePath) {
        return UrlUtils.buildFullRequestUrl(serverScheme, serverName, serverPort, relativePath, null);
    }
}
