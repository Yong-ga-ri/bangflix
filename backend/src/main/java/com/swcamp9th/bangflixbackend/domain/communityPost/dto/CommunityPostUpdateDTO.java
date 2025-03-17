package com.swcamp9th.bangflixbackend.domain.communitypost.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommunityPostUpdateDTO {
    private String title;                   // 제목
    private String content;                 // 내용
}
