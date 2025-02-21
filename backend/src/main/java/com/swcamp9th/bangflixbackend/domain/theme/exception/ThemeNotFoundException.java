package com.swcamp9th.bangflixbackend.domain.theme.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class ThemeNotFoundException extends BusinessException {
    public ThemeNotFoundException() {
        super(ErrorCode.THEME_NOT_FOUND);
    }
}
