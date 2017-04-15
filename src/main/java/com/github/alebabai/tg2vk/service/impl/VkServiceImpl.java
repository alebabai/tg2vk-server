package com.github.alebabai.tg2vk.service.impl;

import com.github.alebabai.tg2vk.domain.Chat;
import com.github.alebabai.tg2vk.domain.ChatType;
import com.github.alebabai.tg2vk.domain.User;
import com.github.alebabai.tg2vk.service.VkService;
import com.github.alebabai.tg2vk.util.constants.VkConstants;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.Actor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.messages.LongpollMessages;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetLongPollHistoryResponse;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollServerQuery;
import com.vk.api.sdk.queries.users.UserField;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class VkServiceImpl implements VkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VkServiceImpl.class);

    private static final String VK_AUTHORIZE_URL_FORMAT = "${vk_auth_url}?client_id=${client_id}&display=${display}&redirect_uri=${redirect_uri}&scope=${scope}&response_type=${response_type}&v=${vk_api_version}";

    private final Integer clientId;
    private final String clientSecret;
    private final Integer fetchDelay;
    private final VkApiClient api;

    @Autowired
    public VkServiceImpl(@Value("${tg2vk.vk.client_id}") Integer clientId,
                         @Value("${tg2vk.vk.client_secret}") String clientSecret,
                         @Value("${tg2vk.vk.service.fetch_delay:5000}") Integer fetchDelay) {
        this.api = new VkApiClient(new HttpTransportClient());
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.fetchDelay = fetchDelay;
    }

    @Override
    public Optional<UserActor> authorize(String code) {
        Optional<UserActor> result = Optional.empty();
        try {
            final UserAuthResponse authResponse = api.oauth()
                    .userAuthorizationCodeFlow(clientId, clientSecret, VkConstants.VK_URL_REDIRECT, code)
                    .execute();
            result = Optional.of(new UserActor(authResponse.getUserId(), authResponse.getAccessToken()));
        } catch (ApiException | ClientException | IllegalStateException e) {
            LOGGER.error("Error during authorization process:", e);
        }
        return result;
    }

    @Override
    public Optional<UserActor> authorize(Integer userId, String token) {
        return Optional.of(new UserActor(userId, token));
    }

    @Override
    public String getAuthorizeUrl(String redirectUrl, String... scopes) {
        try {
            final Map<String, String> params = new HashMap<>();
            params.put("vk_auth_url", VkConstants.VK_URL_AUTHORIZE);
            params.put("client_id", clientId.toString());
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
    public CompletableFuture<Integer> fetchMessages(User user, BiConsumer<com.vk.api.sdk.objects.users.User, Message> consumer) {
        final UserActor actor = new UserActor(user.getVkId(), user.getVkToken());
        return CompletableFuture
                .supplyAsync(() -> triggerMessagesFetching(actor, consumer))
                .whenCompleteAsync((result, error) -> LOGGER.error("Error during vk messages fetching :", error));
    }

    @Override
    public Collection<Chat> findChats(com.github.alebabai.tg2vk.domain.User user, String query) {
        try {
            final UserActor actor = new UserActor(user.getVkId(), user.getVkToken());
            final Gson gson = new Gson();
            return Optional.ofNullable(api.messages()
                    .searchDialogs(actor)
                    .q(query)
                    .fields(UserField.PHOTO_100, UserField.PHOTO_200)
                    .executeAsString())
                    .map(json -> gson.fromJson(json, JsonObject.class))
                    .map(jsonObject -> jsonObject.getAsJsonArray("response"))
                    .map(dialogs -> StreamSupport.stream(dialogs.spliterator(), true)
                            .map(JsonElement::getAsJsonObject)
                            .map(this::getChatFromJson)
                            .sorted((chat1, chat2) -> chat1.getTitle().compareToIgnoreCase(chat2.getTitle()))
                            .collect(Collectors.toList()))
                    .orElse(Collections.emptyList());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    private Chat getChatFromJson(JsonObject json) {
        final int id = json.get("id").getAsInt();
        final String title = Optional.ofNullable(json.get("title"))
                .map(JsonElement::getAsString)
                .orElseGet(() -> {
                    final String firstName = json.get("first_name").getAsString();
                    final String lastName = json.get("last_name").getAsString();
                    return String.join(StringUtils.SPACE, firstName, lastName);
                });
        final ChatType type = Optional.of(json.get("type"))
                .map(JsonElement::getAsString)
                .filter("chat"::equals)
                .map(it -> ChatType.GROUP_CHAT)
                .orElse(ChatType.PRIVATE_CHAT);
        final String photoUrl = Optional.ofNullable(json.get("photo_200"))
                .map(JsonElement::getAsString)
                .orElse(StringUtils.EMPTY);
        final String thumbUrl = Optional.ofNullable(json.get("photo_100"))
                .map(JsonElement::getAsString)
                .orElse(StringUtils.EMPTY);
        return new Chat(id, title, type)
                .setPhotoUrl(photoUrl)
                .setThumbUrl(thumbUrl);
    }

    private int triggerMessagesFetching(Actor actor, BiConsumer<com.vk.api.sdk.objects.users.User, Message> consumer) {
        try {
            final MessagesGetLongPollServerQuery query = api.messages().getLongPollServer(actor).useSsl(true).needPts(true);
            return getMessages(actor, query, consumer);
        } catch (ApiException e) {
            throw new IllegalStateException("VK API error happened during messages fetching", e);
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected error happened during VK messages fetching", e);
        }

    }

    private int getMessages(Actor actor, MessagesGetLongPollServerQuery query, BiConsumer<com.vk.api.sdk.objects.users.User, Message> consumer) throws ClientException, ApiException, InterruptedException {
        int newTs = query.execute().getTs();
        while (!Thread.interrupted()) {
            final GetLongPollHistoryResponse response = api.messages().getLongPollHistory(actor).ts(newTs).execute();
            final LongpollMessages messages = response.getMessages();
            final List<com.vk.api.sdk.objects.users.User> profiles = response.getProfiles();
            newTs = messages.getCount() > 0 ? query.execute().getTs() : newTs;
            messages.getMessages().stream()
                    .filter(message -> !message.isOut())
                    .forEach(message -> profiles.stream()
                            .filter(profile -> profile.getId().equals(message.getUserId()))
                            .forEach(profile -> consumer.accept(profile, message)));
            Thread.sleep(fetchDelay);
        }
        return newTs;
    }
}
