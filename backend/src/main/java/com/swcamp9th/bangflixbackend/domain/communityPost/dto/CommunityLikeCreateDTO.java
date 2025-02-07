package com.swcamp9th.bangflixbackend.domain.communitypost.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommunityLikeCreateDTO {
    private Integer communityPostCode;      // 게시글 코드
}
