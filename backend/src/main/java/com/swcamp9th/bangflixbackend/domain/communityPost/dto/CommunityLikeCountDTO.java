package com.swcamp9th.bangflixbackend.domain.communitypost.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommunityLikeCountDTO {
    private Integer communityPostCode;      // 게시글 코드
    private Long likeCount;                 // 좋아요 개수
}
