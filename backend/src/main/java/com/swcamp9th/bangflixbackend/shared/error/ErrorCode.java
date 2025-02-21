package com.swcamp9th.bangflixbackend.shared.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // General
    FILE_UPLOAD_ERROR(400, "파일 업로드에 실패했습니다."),

    // Theme
    THEME_NOT_FOUND(404, "존재하지 않는 테마입니다."),

    // Review
    REVIEW_NOT_FOUND(404, "존재하지 않는 리뷰입니다."),
    REVIEW_ALREADY_LIKED(400, "이미 좋아요를 누른 리뷰입니다."),
    REVIEW_NOT_LIKED(400, "좋아요를 누르지 않은 리뷰입니다."),

    // Reaction
    UNEXPECTED_REACTION_TYPE(400, "지정되지 않은 타입의 리액션입니다."),
    REACTION_NOT_FOUND(404, "존재하지 않는 리액션입니다."),

    ;

    private final int status;
    private final String message;
}
