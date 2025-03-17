package com.swcamp9th.bangflixbackend.domain.review.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class ReviewNotFoundException extends BusinessException {
    public ReviewNotFoundException() {
        super(ErrorCode.REVIEW_NOT_FOUND);
    }
}
