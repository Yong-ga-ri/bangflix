package com.swcamp9th.bangflixbackend.domain.theme.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class UnexpectedReactionTypeException extends BusinessException {
    public UnexpectedReactionTypeException(String message) {
        super(ErrorCode.UNEXPECTED_REACTION_TYPE, message);
    }
}
