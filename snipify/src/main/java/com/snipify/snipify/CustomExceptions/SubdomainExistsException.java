package com.snipify.snipify.CustomExceptions;

public class SubdomainExistsException extends RuntimeException {
    public SubdomainExistsException(String message) {
        super(message);
    }
}
