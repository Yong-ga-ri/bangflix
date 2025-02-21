package com.swcamp9th.bangflixbackend.shared.error;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
