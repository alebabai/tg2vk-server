package com.github.alebabai.tg2vk.util.constants;


public final class VkConstants {
    public static final String VK_URL_OAUTH_BASE = "https://oauth.vk.com";
    public static final String VK_URL_AUTHORIZE = VK_URL_OAUTH_BASE + "/createOrUpdate";
    public static final String VK_URL_REDIRECT = VK_URL_OAUTH_BASE + "/blank.html";

    public static final String VK_API_VERSION = "5.59";
    public static final String VK_RESPONSE_TYPE_CODE = "code";
    public static final String VK_RESPONSE_TYPE_TOKEN = "token";

    public static final String VK_DISPLAY_TYPE_PAGE = "page";
    public static final String VK_DISPLAY_TYPE_POPUP = "popup";
    public static final String VK_DISPLAY_TYPE_MOBILE = "mobile";

    public static final String VK_SCOPE_NOTIFY = "notify";
    public static final String VK_SCOPE_FRIENDS = "friends";
    public static final String VK_SCOPE_PHOTOS = "photos";
    public static final String VK_SCOPE_AUDIO = "audio";
    public static final String VK_SCOPE_VIDEO = "video";
    public static final String VK_SCOPE_PAGES = "pages";
    public static final String VK_SCOPE_NOTES = "notes";
    public static final String VK_SCOPE_STATUS = "status";
    public static final String VK_SCOPE_MESSAGES = "messages";
    public static final String VK_SCOPE_WALL = "wall";
    public static final String VK_SCOPE_ADS = "ads";
    public static final String VK_SCOPE_OFFLINE = "offline";
    public static final String VK_SCOPE_DOCS = "docs";
    public static final String VK_SCOPE_GROUPS = "groups";
    public static final String VK_SCOPE_NOTIFICATIONS = "notifications";
    public static final String VK_SCOPE_STATS = "stats";
    public static final String VK_SCOPE_EMAIL = "email";
    public static final String VK_SCOPE_MARKET = "market";
    public static final String VK_SCOPE_NOHTTPS = "nohttps";

    private VkConstants() {
        super();
    }
}
