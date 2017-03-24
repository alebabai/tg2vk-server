package com.github.alebabai.tg2vk.service;

public interface TemplateRenderer {
    <T> String render(String templateName, T context);
}
