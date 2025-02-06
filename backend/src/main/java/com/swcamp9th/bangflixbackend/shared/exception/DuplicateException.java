package com.swcamp9th.bangflixbackend.shared.exception;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
