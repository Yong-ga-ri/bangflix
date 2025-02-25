package com.swcamp9th.bangflixbackend.shared.error.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;

public class InvalidUserException extends BusinessException {
    public InvalidUserException() {
        super(ErrorCode.INVALID_USER);
    }
}
