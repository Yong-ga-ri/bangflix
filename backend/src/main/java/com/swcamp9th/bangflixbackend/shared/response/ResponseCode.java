package com.swcamp9th.bangflixbackend.shared.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    OK(200, "성공"),
    CREATED(201, "생성 성공"),
    UPDATED(204, "수정 성공"),
    DELETED(204, "삭제 성공")
    ;

    private final int code;
    private final String message;
}
