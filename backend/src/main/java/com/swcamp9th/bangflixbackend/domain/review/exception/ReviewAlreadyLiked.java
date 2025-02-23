package com.swcamp9th.bangflixbackend.domain.review.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class ReviewAlreadyLiked extends BusinessException {
    public ReviewAlreadyLiked() {
        super(ErrorCode.REVIEW_ALREADY_LIKED);
    }
}
