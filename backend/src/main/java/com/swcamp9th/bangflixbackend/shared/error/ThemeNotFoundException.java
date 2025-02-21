package com.swcamp9th.bangflixbackend.shared.error;

public class ThemeNotFoundException extends RuntimeException {
    public ThemeNotFoundException(String message) {
        super(message);
    }
}
