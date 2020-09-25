package com.antonio.authserver.utils;

public class SecurityConstants {


    public static final Long TOKEN_EXPIRATION_TIME = 36000L;//180000L; // 30 MIN
    public static final Long REFRESH_TOKEN_EXPIRATION_TIME = 604800000L; // 7 days
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final String BASIC_TOKEN_PREFIX = "Basic ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_SECRET = "ld3x2od2oska1nzc";
    public static final String REFRESH_TOKEN_SECRET = "32d23dadcce23";
}

