package com.swcamp9th.bangflixbackend.domain.review.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class ReviewNotLikedException extends BusinessException {
    public ReviewNotLikedException() {
        super(ErrorCode.REVIEW_NOT_LIKED);
    }
}
