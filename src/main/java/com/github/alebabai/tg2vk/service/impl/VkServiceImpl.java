package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.Constants;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class VkServiceImpl implements VkService {

    @Autowired
    private VkApiClient vkApi;

    @Autowired
    Environment env;

    @Override
    public UserActor authorize(String code) {
        UserAuthResponse authResponse = null;
        try {
            authResponse = vkApi.oauth()
                    .userAuthorizationCodeFlow(
                            env.getRequiredProperty(Constants.PROP_VK_CLIENT_ID, Integer.class),
                            env.getRequiredProperty(Constants.PROP_VK_CLIENT_SECRET),
                            env.getRequiredProperty(Constants.PROP_APP_DOMAIN), code)
                    .execute();
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }

        return new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
    }

    @Override
    public boolean isAuthorized() {
        return false;
    }
}
