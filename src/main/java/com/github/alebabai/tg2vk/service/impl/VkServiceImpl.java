package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.PathResolverService;
import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.constants.Constants;
import com.github.alebabai.tg2vk.util.constants.PathConstants;
import com.github.alebabai.tg2vk.util.constants.VkConstants;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
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

    private static final String VK_AUTHORIZE_URL_FORMAT = "${vk_auth_url}?client_id=${client_id}&display=${display}&redirect_uri=${redirect_uri}&scope=${scope}&response_type=${response_type}&v=${vk_api_version}";
    private static final String LOCAL_AUTHORIZE_URL_FORMAT = "${server_url}${local_redirect_path}";

    @Autowired
    private VkApiClient vkApi;

    @Autowired
    private Environment env;

    @Autowired
    private PathResolverService pathResolver;

    private UserActor actor;

    @Override
    public void authorize(String code) {
        try {
            UserAuthResponse authResponse = vkApi.oauth()
                    .userAuthorizationCodeFlow(
                            env.getRequiredProperty(Constants.PROP_VK_CLIENT_ID, Integer.class),
                            env.getRequiredProperty(Constants.PROP_VK_CLIENT_SECRET),
                            VkConstants.VK_URL_REDIRECT,
                            code)
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
    public String getAuthorizeUrl(boolean allowAll) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("vk_auth_url", VkConstants.VK_URL_AUTHORIZE);
            params.put("client_id", env.getRequiredProperty(Constants.PROP_VK_CLIENT_ID));
            params.put("display", VkConstants.VK_DISPLAY_TYPE_PAGE);
            params.put("redirect_uri", allowAll ? VkConstants.VK_URL_REDIRECT : getLocalRedirectUrl());
            params.put("scope",
                    String.format("%s,%s,%s",
                            VkConstants.VK_SCOPE_NOTIFY,
                            VkConstants.VK_SCOPE_NOTIFICATIONS,
                            VkConstants.VK_SCOPE_OFFLINE));
            params.put("response_type", VkConstants.VK_RESPONSE_TYPE_CODE);
            params.put("vk_api_version", VkConstants.VK_API_VERSION);

            return StrSubstitutor.replace(VK_AUTHORIZE_URL_FORMAT, params);
        } catch (IllegalStateException e) {
            LOGGER.error("Error during authorize url generation :", e);
        }
        return StringUtils.EMPTY;
    }


    /**
     * Should be deleted later (authorization only through mobile or not)
     */
    @Deprecated
    private String getLocalRedirectUrl() {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("server_url", pathResolver.getServerUrl());
            params.put("local_redirect_path", PathConstants.AUTHORIZE_PATH);

            return StrSubstitutor.replace(LOCAL_AUTHORIZE_URL_FORMAT, params);
        } catch (IllegalStateException e) {
            LOGGER.error("Error during local redirect url generation :", e);
        }
        return StringUtils.EMPTY;
    }
}
