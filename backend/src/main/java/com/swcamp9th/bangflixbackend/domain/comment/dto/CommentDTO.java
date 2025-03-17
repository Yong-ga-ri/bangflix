package com.swcamp9th.bangflixbackend.domain.comment.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommentDTO {
    private Integer commentCode;            // 댓글 코드
    private Boolean active;                 // 활성화 여부
    private String createdAt;               // 생성일시(작성일시)
    private String content;                 // 댓글 내용
    private String nickname;                // 회원 닉네임(작성자)
    private Integer communityPostCode;      // 게시글 코드(댓글이 작성된 게시글)
    private String profile;                 // 회원 프로필 사진
}
