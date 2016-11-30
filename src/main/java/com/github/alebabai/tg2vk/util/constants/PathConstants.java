package com.github.alebabai.tg2vk.util.constants;

public final class PathConstants {
    public static final String ROOT = "/";

    public static final String PAGE_ERROR = "/error";

    public static final String API = "/api";

    public static final String API_AUTHORIZATION = API + "/auth";
    public static final String API_LOGIN = API_AUTHORIZATION + "/login";
    public static final String API_AUTHORIZE = API_AUTHORIZATION + "/authorize";

    public static final String API_TELEGRAM = API + "/telegram";
    public static final String API_TELEGRAM_FETCH_UPDATES = API_TELEGRAM + "/fetch-updates";

    private PathConstants(){
        super();
    }
}
