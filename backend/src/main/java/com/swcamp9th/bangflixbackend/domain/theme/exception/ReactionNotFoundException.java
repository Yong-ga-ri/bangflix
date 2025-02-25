package com.swcamp9th.bangflixbackend.domain.theme.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class ReactionNotFoundException extends BusinessException {
    public ReactionNotFoundException() {
        super(ErrorCode.REACTION_NOT_FOUND);
    }
}
