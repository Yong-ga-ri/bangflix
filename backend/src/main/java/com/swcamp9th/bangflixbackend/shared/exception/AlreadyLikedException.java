package com.swcamp9th.bangflixbackend.shared.exception;

// 좋아요가 이미 존재할 때 발생하는 예외
public class AlreadyLikedException extends RuntimeException {
    public AlreadyLikedException(String message) {
        super(message);
    }
}