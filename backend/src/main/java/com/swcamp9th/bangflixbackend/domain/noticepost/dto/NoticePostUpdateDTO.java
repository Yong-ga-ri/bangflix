package com.swcamp9th.bangflixbackend.domain.noticepost.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NoticePostUpdateDTO {

    private String title;                   // 제목
    private String content;                 // 게시글 내용
}
