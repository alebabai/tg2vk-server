package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Service;

@Service
public class PathResolverImpl implements PathResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathResolverImpl.class);

    @Value("${tg2vk.client.url}")
    private String clientBaseUrl;

    @Value("${tg2vk.server.scheme}")
    private String serverScheme;

    @Value("${tg2vk.server.name}")
    private String serverName;

    @Value("${tg2vk.server.port}")
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
