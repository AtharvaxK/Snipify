package com.snipify.snipify.CustomExceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class LinkExpiredException  extends RuntimeException{
    public LinkExpiredException(String message){
        super(message);
    }
}
