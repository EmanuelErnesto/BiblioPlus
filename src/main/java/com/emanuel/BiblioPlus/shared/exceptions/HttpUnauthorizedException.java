package com.emanuel.BiblioPlus.shared.exceptions;

public class HttpUnauthorizedException extends RuntimeException {
    public HttpUnauthorizedException(String message) {
        super(message);
    }
}
