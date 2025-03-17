package com.swcamp9th.bangflixbackend.domain.theme.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class ThemeReactionNotFoundException extends BusinessException {
    public ThemeReactionNotFoundException() {
        super(ErrorCode.THEME_REACTION_NOT_FOUND);
    }
}
