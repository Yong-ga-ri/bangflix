package com.swcamp9th.bangflixbackend.shared.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SuccessResponse<T> {
    private final int status;
    private final String message;
    private final T data;

    public static <T> SuccessResponse<T> of(ResponseCode responseCode, T data) {
        return new SuccessResponse<>(
                responseCode.getCode(),
                responseCode.getMessage(),
                data
        );
    }

    public static SuccessResponse<Void> empty(ResponseCode responseCode, String message) {
        return new SuccessResponse<>(
                responseCode.getCode(),
                message,
                null
        );
    }

    public static SuccessResponse<Void> empty(ResponseCode responseCode) {
        return new SuccessResponse<>(
                responseCode.getCode(),
                responseCode.getMessage(),
                null
        );
    }

    public SuccessResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
