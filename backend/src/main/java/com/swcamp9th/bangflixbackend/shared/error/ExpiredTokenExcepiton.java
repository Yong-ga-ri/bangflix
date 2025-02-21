package com.swcamp9th.bangflixbackend.shared.error;

public class ExpiredTokenExcepiton  extends RuntimeException {
    public ExpiredTokenExcepiton(String message) {
        super(message);
    }
}
