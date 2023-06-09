package com.effectivemobile.socialmedia.exeption;

public class EmptyMessageListException extends RuntimeException {
    public EmptyMessageListException(String message) {
        super(message);
    }
}
