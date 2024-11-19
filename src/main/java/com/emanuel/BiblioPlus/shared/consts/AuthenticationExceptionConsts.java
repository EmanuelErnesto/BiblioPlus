package com.emanuel.BiblioPlus.shared.consts;

public class AuthenticationExceptionConsts {
    public static final String INVALID_USER = "The email that you provided does not match with a registered user";
    public static final String INVALID_REFRESH_TOKEN = "You must provide a refresh token. Try again with a valid token";
    public static final String INVALID_ACCESS_TOKEN = "You must provide a access token. Try again with a valid token";
    public static final String REFRESH_TOKEN_NOT_FOUND = "Refresh token not found";
    public static final String REFRESH_TOKEN_REVOKED = "Refresh token is revoked. Try again with a valid token";
    public static final String INVALID_USER_CREDENTIALS = "The original user that provided this token cannot be found. Provide a token of an existent user";
}
