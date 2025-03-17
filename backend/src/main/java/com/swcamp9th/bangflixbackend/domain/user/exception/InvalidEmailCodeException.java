package com.swcamp9th.bangflixbackend.domain.user.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

// 이메일 인증 중 에러
public class InvalidEmailCodeException extends BusinessException {
    public InvalidEmailCodeException() {
        super(ErrorCode.INVALID_EMAIL_CODE_EXCEPTION);
    }
    public InvalidEmailCodeException(String message) {
        super(ErrorCode.INVALID_EMAIL_CODE_EXCEPTION, message);
    }
}
