package com.snipify.snipify.CustomExceptions;

public class OtpAttemptsOver extends RuntimeException {
    public OtpAttemptsOver(String message) {
        super(message);
    }
}
