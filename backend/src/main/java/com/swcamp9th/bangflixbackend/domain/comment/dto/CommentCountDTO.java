package com.swcamp9th.bangflixbackend.domain.comment.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommentCountDTO {
    private Integer communityPostCode;      // 게시글 코드
    private Long commentCount;              // 댓글 개수
}
