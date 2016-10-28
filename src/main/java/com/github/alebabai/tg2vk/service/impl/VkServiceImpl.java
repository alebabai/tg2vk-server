package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.constants.Constants;
import com.github.alebabai.tg2vk.util.constants.VkConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetResponse;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollServerQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static com.github.alebabai.tg2vk.util.constants.Constants.PROP_VK_FETCH_DELAY;

@Service
public class VkServiceImpl implements VkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VkService.class);

    private static final String VK_AUTHORIZE_URL_FORMAT = "${vk_auth_url}?client_id=${client_id}&display=${display}&redirect_uri=${redirect_uri}&scope=${scope}&response_type=${response_type}&v=${vk_api_version}";

    private final Environment env;
    private final VkApiClient api;
    private final Gson gson;

    private UserActor actor;//Temp

    @Autowired
    private VkServiceImpl(Environment environment) {
        this.env = environment;
        this.api = new VkApiClient(new HttpTransportClient());
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void authorize(String code) {
        try {
            final UserAuthResponse authResponse = api.oauth()
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
    public String getAuthorizeUrl(String redirectUrl, String... scopes) {
        try {
            final Map<String, String> params = new HashMap<>();
            params.put("vk_auth_url", VkConstants.VK_URL_AUTHORIZE);
            params.put("client_id", env.getRequiredProperty(Constants.PROP_VK_CLIENT_ID));
            params.put("display", VkConstants.VK_DISPLAY_TYPE_POPUP);
            params.put("redirect_uri", redirectUrl);
            params.put("scope", StringUtils.join(scopes, ","));
            params.put("response_type", VkConstants.VK_RESPONSE_TYPE_CODE);
            params.put("vk_api_version", VkConstants.VK_API_VERSION);
            return StrSubstitutor.replace(VK_AUTHORIZE_URL_FORMAT, params);
        } catch (IllegalStateException e) {
            LOGGER.error("Error during authorize url generation :", e);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public void fetchMessages(BiConsumer<? super User, ? super Message> callback) {
        CompletableFuture.runAsync(() -> {
            try {
                //TODO get user_id and token from DB
                final Integer userId = env.getRequiredProperty("user_id", Integer.class);
                final String token = env.getRequiredProperty("token");
                final UserActor userActor = new UserActor(userId, token);
                final MessagesGetLongPollServerQuery query = api.messages().getLongPollServer(userActor).useSsl(true).needPts(true);
                getMessages(userActor, query, query.execute().getTs(), callback);
            } catch (ApiException | ClientException | InterruptedException e) {
                LOGGER.error("Error during vk messages fetching :", e);
            }
        });
    }

    private void getMessages(Actor actor, MessagesGetLongPollServerQuery query, int ts, BiConsumer<? super User, ? super Message> callback) throws ClientException, ApiException, InterruptedException {
        final String textResponse = api.messages().getLongPollHistory(actor).ts(ts).executeAsString();
        final JsonObject response = ((JsonObject) new JsonParser().parse(textResponse)).get("response").getAsJsonObject();
        final GetResponse messages = gson.fromJson(response.get("messages"), GetResponse.class);
        final Type listType = new TypeToken<ArrayList<User>>() {}.getType();
        final List<User> profiles = gson.fromJson(response.get("profiles"), listType);
        final int newTs = messages.getCount() > 0 ? query.execute().getTs() : ts;
        messages.getItems().stream()
                .filter(message -> !message.isOut())
                .forEach(message -> profiles.stream()
                        .filter(user -> user.getId().equals(message.getUserId()))
                        .findAny()
                        .ifPresent(user -> callback.accept(user, message)));
        Thread.sleep(env.getProperty(PROP_VK_FETCH_DELAY, Integer.class, 1000));
        getMessages(actor, query, newTs, callback);
    }
}
