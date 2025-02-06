package com.swcamp9th.bangflixbackend.shared.exception;

// 좋아요가 존재하지 않을 때 발생하는 예외
public class LikeNotFoundException extends RuntimeException {
    public LikeNotFoundException(String message) {
        super(message);
    }
}
