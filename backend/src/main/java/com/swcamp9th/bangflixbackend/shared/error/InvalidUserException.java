package com.swcamp9th.bangflixbackend.shared.error;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String message) {
        super(message);
    }
}
