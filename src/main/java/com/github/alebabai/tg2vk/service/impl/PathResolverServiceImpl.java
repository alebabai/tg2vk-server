package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.PathResolverService;
import com.github.alebabai.tg2vk.util.constants.Constants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.eclipse.jetty.http.HttpScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PathResolverServiceImpl implements PathResolverService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathResolverService.class);
    private static final String SERVER_URL_FORMAT = "${scheme}://${server_name}:${server_host_port}";

    @Autowired
    public Environment env;

    @Override
    public String getServerUrl() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("scheme", HttpScheme.HTTPS.toString());
            params.put("server_name", env.getRequiredProperty(Constants.PROP_SERVER_NAME));
            params.put("server_host_port", env.getRequiredProperty(Constants.PROP_SERVER_HOST_PORT));

            return StrSubstitutor.replace(SERVER_URL_FORMAT, params);
        } catch (IllegalStateException e) {
            LOGGER.error("Error during server url generation", e);
        }
        return StringUtils.EMPTY;
    }
}
