package com.imap143.realworld.exception;

public class RealWorldException extends RuntimeException {
    public RealWorldException(String message) {
        super(message);
    }

    public RealWorldException(String message, Throwable cause) {
        super(message, cause);
    }
} 