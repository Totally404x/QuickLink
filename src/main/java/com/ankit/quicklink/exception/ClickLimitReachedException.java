package com.ankit.quicklink.exception;

public class ClickLimitReachedException extends RuntimeException {

    public ClickLimitReachedException(String message) {
        super(message);
    }
}
