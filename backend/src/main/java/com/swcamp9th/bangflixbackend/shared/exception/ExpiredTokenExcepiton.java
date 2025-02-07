package com.swcamp9th.bangflixbackend.shared.exception;

public class ExpiredTokenExcepiton  extends RuntimeException {
    public ExpiredTokenExcepiton(String message) {
        super(message);
    }
}
