package com.swcamp9th.bangflixbackend.shared.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // General
    FILE_UPLOAD_ERROR(400, "파일 업로드에 실패했습니다."),

    // User
    MEMBER_NOT_FOUND(404, "존재하지 않는 회원입니다."),
    DUPLICATE_ID(400, "이미 존재하는 아이디입니다."),
    DUPLICATE_NICKNAME(400, "이미 존재하는 닉네임입니다."),
    DUPLICATE_EMAIL(400, "이미 존재하는 이메일입니다."),
    PASSWORD_NOT_MATCHED(400, "비밀번호가 일치하지 않습니다."),

    // Auth
    INVALID_EMAIL_CODE_EXCEPTION(400, "이메일 인증에 실패했습니다."),
    SEND_EMAIL_FAILED(400, "인증 이메일 발송에 실패했습니다."),
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),
    TOKEN_INVALID(401, "유효하지 않은 토큰입니다."),
    LOGIN_REQUIRED(401, "로그인이 필요합니다."),
    INVALID_USER(401, "유효하지 않은 사용자입니다."),

    // Store
    STORE_NOT_FOUND(404, "존재하지 않는 업체입니다."),

    // Theme
    THEME_NOT_FOUND(404, "존재하지 않는 테마입니다."),
    GENRE_NOT_FOUND(404, "존재하지 않는 장르입니다."),
    REACTION_NOT_FOUND(404, "존재하지 않는 리액션입니다."),

    // Review
    REVIEW_NOT_FOUND(404, "존재하지 않는 리뷰입니다."),
    REVIEW_ALREADY_LIKED(400, "이미 좋아요를 누른 리뷰입니다."),
    REVIEW_NOT_LIKED(400, "좋아요를 누르지 않은 리뷰입니다."),

    // Reaction
    UNEXPECTED_REACTION_TYPE(400, "지정되지 않은 타입의 리액션입니다."),
    THEME_REACTION_NOT_FOUND(404, "존재하지 않는 리액션입니다."),

    // EventPost
    EVENT_POST_NOT_FOUND(404, "존재하지 않는 이벤트 게시글입니다."),

    // NoticePost
    NOTICE_POST_NOT_FOUND(404, "존재하지 않는 공지사항 게시글입니다."),

    // Community
    COMMUNITY_POST_NOT_FOUND(404, "존재하지 않는 게시글입니다."),

    // Comment
    COMMENT_NOT_FOUND(404, "존재하지 않는 댓글입니다."),

    // Internal Server Error
    INTERNAL_SERVER_ERROR(500, "시스템 에러입니다. 관리자에게 문의하세요."),
    BAD_REQUEST(400, "잘못된 요청입니다.");

    private final int status;
    private final String message;
}
