package com.swcamp9th.bangflixbackend.domain.theme.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class GenreNotFoundException extends BusinessException {
    public GenreNotFoundException() {
        super(ErrorCode.GENRE_NOT_FOUND);
    }
}
