package com.swcamp9th.bangflixbackend.domain.user.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class EmailSendException extends BusinessException {
    public EmailSendException() {
        super(ErrorCode.SEND_EMAIL_FAILED);
    }
}
