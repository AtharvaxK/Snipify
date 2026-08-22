package com.snipify.snipify.CustomExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class OtpAttemptsOver extends RuntimeException {
    public OtpAttemptsOver(String message) {
        super(message);
    }
}
