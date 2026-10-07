package com.ankit.quicklink.exception;

public class LinkExpiredException extends RuntimeException{

    public LinkExpiredException(String message) {
        super(message);
    }
}
