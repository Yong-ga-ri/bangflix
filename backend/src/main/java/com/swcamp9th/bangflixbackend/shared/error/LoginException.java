package com.swcamp9th.bangflixbackend.shared.error;

// 로그인 중 발생하는 에러
public class LoginException  extends RuntimeException{
    public LoginException(String message) {
        super(message);
    }
}
