package com.github.alebabai.tg2vk.service.core;

public interface PathResolver {
    String getClientUrl();

    String getServerUrl();

    String resolveServerUrl(String relativePath);

    String resolveClientUrl(String relativePath);
}
