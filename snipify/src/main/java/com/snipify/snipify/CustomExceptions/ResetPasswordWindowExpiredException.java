package com.snipify.snipify.CustomExceptions;

public class ResetPasswordWindowExpiredException extends RuntimeException {
    public ResetPasswordWindowExpiredException(String message) {
        super(message);
    }
}
