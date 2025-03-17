package com.swcamp9th.bangflixbackend.domain.eventPost.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class EventPostNotFoundException extends BusinessException {
    public EventPostNotFoundException() {
        super(ErrorCode.EVENT_POST_NOT_FOUND);
    }
}
