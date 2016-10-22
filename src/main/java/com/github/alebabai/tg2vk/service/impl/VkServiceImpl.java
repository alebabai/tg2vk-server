package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.Constants;
import com.github.alebabai.tg2vk.util.VkConstants;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
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
public class VkServiceImpl implements VkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VkService.class);

    private static final String AUTHORIZE_URL_FORMAT = "${vk_auth_url}?client_id=${client_id}&display=${display}&redirect_uri=${redirect_uri}&scope=${scope}&response_type=${response_type}&v=${vk_api_version}";
    private static final String VK_REDIRECT_URL_FORMAT = "${scheme}://${server_name}:${server_host_port}/${vk_redirect_path}";

    @Autowired
    private VkApiClient vkApi;

    @Autowired
    private Environment env;

    private UserActor actor;

    @Override
    public void authorize(String code) {
        try {
            UserAuthResponse authResponse = vkApi.oauth()
                    .userAuthorizationCodeFlow(
                            env.getRequiredProperty(Constants.PROP_VK_CLIENT_ID, Integer.class),
                            env.getRequiredProperty(Constants.PROP_VK_CLIENT_SECRET),
                            getRedirectUrl(), code)
                    .execute();
            actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
        } catch (ApiException | ClientException | IllegalStateException e) {
            LOGGER.error("Error during authorization process:", e);
        }
    }

    @Override
    public boolean isAuthorized() {
        return actor != null;
    }

    @Override
    public String getAuthorizeUrl() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("vk_auth_url", VkConstants.VK_AUTHORIZE_URL);
            params.put("client_id", env.getRequiredProperty(Constants.PROP_VK_CLIENT_ID));
            params.put("display", VkConstants.VK_DISPLAY_TYPE_PAGE);
            params.put("redirect_uri", getRedirectUrl());
            params.put("scope",
                    String.format("%s,%s,%s",
                            VkConstants.VK_SCOPE_NOTIFY,
                            VkConstants.VK_SCOPE_NOTIFICATIONS,
                            VkConstants.VK_SCOPE_OFFLINE));
            params.put("response_type", VkConstants.VK_RESPONSE_TYPE_CODE);
            params.put("vk_api_version", VkConstants.VK_API_VERSION);

            return StrSubstitutor.replace(AUTHORIZE_URL_FORMAT, params);
        } catch (IllegalStateException e) {
            LOGGER.error("Error during authorize url generation :", e);
        }
        return StringUtils.EMPTY;
    }

    private String getRedirectUrl() {
        Map<String, String> params = new HashMap<>();
        params.put("scheme", HttpScheme.HTTP.toString());
        params.put("server_name", env.getRequiredProperty(Constants.PROP_SERVER_NAME));
        params.put("server_host_port", env.getRequiredProperty(Constants.PROP_SERVER_HOST_PORT));
        params.put("vk_redirect_path", env.getRequiredProperty(Constants.PROP_VK_REDIRECT_PATH));

        return StrSubstitutor.replace(VK_REDIRECT_URL_FORMAT, params);
    }
}
