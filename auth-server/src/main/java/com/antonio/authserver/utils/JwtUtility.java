package com.antonio.authserver.utils;

public class JwtUtility {

    public static Long getTokenExpirationTime() {
        return System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION_TIME;
    }

    public static Long getRefreshTokenExpirationTime() {
        return System.currentTimeMillis() + SecurityConstants.REFRESH_TOKEN_EXPIRATION_TIME;
    }
}
