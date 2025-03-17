package com.swcamp9th.bangflixbackend.domain.eventPost.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EventPostCreateDTO {

    private String title;                   // 제목
    private String content;                 // 게시글 내용
    private String category;                // 카테고리(할인테마/신규테마)
    private Integer themeCode;              // 테마 코드
}
