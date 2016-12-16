package com.github.alebabai.tg2vk.service;

public interface PathResolverService {
    String getServerUrl();
    String getAbsoluteUrl(String relativePath);
}
