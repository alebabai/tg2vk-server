package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.TemplateRenderer;
import com.samskivert.mustache.Mustache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mustache.MustacheResourceTemplateLoader;
import org.springframework.stereotype.Service;

import java.io.Reader;

@Service
public class TemplateRendererServiceImpl implements TemplateRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateRendererServiceImpl.class);

    private final MustacheResourceTemplateLoader templateLoader;

    @Autowired
    public TemplateRendererServiceImpl(MustacheResourceTemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    @Override
    public <T> String render(String templateName, T context) {
        String result = StringUtils.EMPTY;
        try {
            final Reader source = templateLoader.getTemplate(templateName);
            result = Mustache.compiler().escapeHTML(false).compile(source).execute(context);
        } catch (Exception e) {
            LOGGER.error("Error during template rendering", e);
        }
        return result;
    }
}
