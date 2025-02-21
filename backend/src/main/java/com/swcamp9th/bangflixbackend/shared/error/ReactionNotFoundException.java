package com.swcamp9th.bangflixbackend.shared.error;

public class ReactionNotFoundException extends RuntimeException {
    public ReactionNotFoundException(String message) {
        super(message);
    }
}
