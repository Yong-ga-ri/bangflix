package com.swcamp9th.bangflixbackend.shared.error;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ErrorResponse {
    private final int status;
    private final String msg;

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode, message);
    }

    public ErrorResponse(ErrorCode errorCode, String message) {
        this.status = errorCode.getStatus();
        this.msg = message;
    }

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.msg = errorCode.getMessage();
    }
}
