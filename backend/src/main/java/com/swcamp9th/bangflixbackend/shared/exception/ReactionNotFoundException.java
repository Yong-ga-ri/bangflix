package com.swcamp9th.bangflixbackend.shared.exception;

public class ReactionNotFoundException extends RuntimeException {
    public ReactionNotFoundException(String message) {
        super(message);
    }
}
