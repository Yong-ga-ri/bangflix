package com.swcamp9th.bangflixbackend.shared.error.exception;

import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import lombok.Getter;

import java.util.List;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getMessage();
    }

    public BusinessException(ErrorCode errorCode, String errorMessage) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BusinessException(ErrorCode errorCode, List<String> errorMessages) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = String.join("\n", errorMessages);
    }
}
