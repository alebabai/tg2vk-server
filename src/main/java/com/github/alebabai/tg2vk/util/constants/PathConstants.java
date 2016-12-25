package com.github.alebabai.tg2vk.util.constants;

public final class PathConstants {
    public static final String ROOT = "/";

    public static final String PAGE_ERROR = "/error";

    public static final String API = "/api";

    public static final String API_AUTH = API + "/auth";
    public static final String API_AUTH_LOGIN = API_AUTH + "/login";
    public static final String API_AUTH_AUTHORIZE = API_AUTH + "/authorize";
    public static final String API_AUTH_AUTHORIZE_IMPLICIT = API_AUTH + "/authorize/implicit";
    public static final String API_AUTH_AUTHORIZE_CODE = API_AUTH + "/authorize/code";

    public static final String API_TELEGRAM = API + "/telegram";
    public static final String API_TELEGRAM_FETCH_UPDATES = API_TELEGRAM + "/fetch-updates";

    private PathConstants() {
        super();
    }
}
