package com.snipify.snipify.CustomExceptions;



public class LinkNotFoundException extends RuntimeException{
    public LinkNotFoundException(String message){
        super(message);
    }
}
