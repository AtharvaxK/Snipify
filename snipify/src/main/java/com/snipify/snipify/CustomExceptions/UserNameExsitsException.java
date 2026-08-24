package com.snipify.snipify.CustomExceptions;

public class UserNameExsitsException extends RuntimeException {
    public UserNameExsitsException(String message) {
        super(message);
    }
}
