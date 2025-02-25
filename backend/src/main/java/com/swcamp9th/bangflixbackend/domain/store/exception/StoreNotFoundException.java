package com.swcamp9th.bangflixbackend.domain.store.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;

public class StoreNotFoundException extends BusinessException {
    public StoreNotFoundException() {
        super(ErrorCode.STORE_NOT_FOUND);
    }
}
