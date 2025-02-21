package com.swcamp9th.bangflixbackend.domain.noticepost.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class NoticePostNotFoundException extends BusinessException {
    public NoticePostNotFoundException() {
        super(ErrorCode.NOTICE_POST_NOT_FOUND);
    }
}
