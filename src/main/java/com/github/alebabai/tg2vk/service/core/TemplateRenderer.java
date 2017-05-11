package com.github.alebabai.tg2vk.service.core;

public interface TemplateRenderer {
    <T> String render(String templateName, T context);
}
