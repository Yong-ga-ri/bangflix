package com.swcamp9th.bangflixbackend.shared.error.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import lombok.Getter;

@Getter
public class AuthenticationException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;

    public AuthenticationException(String errorMessage) {
        super(errorMessage);
        this.errorCode = ErrorCode.INVALID_USER;
        this.errorMessage = errorMessage;
    }
}
