package com.swcamp9th.bangflixbackend.shared.error;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
