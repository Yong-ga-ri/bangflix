package com.swcamp9th.bangflixbackend.domain.communitypost.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class CommunityPostNotFoundException extends BusinessException {
    public CommunityPostNotFoundException() {
        super(ErrorCode.COMMUNITY_POST_NOT_FOUND);
    }
}
