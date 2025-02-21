package com.swcamp9th.bangflixbackend.shared.error;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException(String message) {
        super(message);
    }
}
