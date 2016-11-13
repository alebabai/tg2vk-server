package com.github.alebabai.tg2vk.util.constants;

public interface PathConstants {
    String ROOT = "/";

    String PAGE_ERROR = "/error";

    String API = "/api";

    String API_AUTHORIZATION = API + "/auth";
    String API_LOGIN = API_AUTHORIZATION + "/login";
    String API_AUTHORIZE = API_AUTHORIZATION + "/authorize";

    String API_TELEGRAM = API + "/telegram";
    String API_TELEGRAM_FETCH_UPDATES = API_TELEGRAM + "/fetch-updates";
}
