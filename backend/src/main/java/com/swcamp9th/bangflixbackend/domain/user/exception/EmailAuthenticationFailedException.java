package com.swcamp9th.bangflixbackend.domain.user.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class EmailAuthenticationFailedException extends BusinessException {
    public EmailAuthenticationFailedException() {
        super(ErrorCode.INVALID_EMAIL_CODE_EXCEPTION);
    }
}
