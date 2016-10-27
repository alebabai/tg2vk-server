package com.github.alebabai.tg2vk.service;

public interface TemplateRendererService {
    <T> String render(String templateName, T context);
}
