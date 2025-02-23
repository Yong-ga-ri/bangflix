package com.swcamp9th.bangflixbackend.shared.error.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;

public class LoginRequiredException extends BusinessException {

    public LoginRequiredException() {
        super(ErrorCode.LOGIN_REQUIRED);
    }
}
