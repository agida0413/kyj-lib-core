package com.kyj.core.security.auth.constants;

public final class RedisKeys {
    private RedisKeys() {}

    public static final String JWT_REFRESH_TOKEN = "jwt:refresh:";
    public static final String JWT_BLACKLIST_TOKEN = "jwt:blacklist:";
    public static final String JWT_BLACKLIST_USER = "jwt:blacklist:user:";

    public static String refreshTokenKey(String userId) {
        return JWT_REFRESH_TOKEN + userId;
    }

    public static String blacklistTokenKey(String token) {
        return JWT_BLACKLIST_TOKEN + token;
    }

    public static String blacklistUserKey(String userId) {
        return JWT_BLACKLIST_USER + userId;
    }
}