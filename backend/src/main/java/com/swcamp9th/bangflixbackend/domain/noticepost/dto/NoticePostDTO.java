package com.swcamp9th.bangflixbackend.domain.noticepost.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NoticePostDTO {

    private Integer noticePostCode;         // 게시글 코드
    private Boolean active;                 // 활성화 여부
    private String createdAt;               // 생성일시(작성일시)
    private String title;                   // 제목
    private String content;                 // 게시글 내용
    private String nickname;                // 회원 닉네임(작성자)

    // 첨부파일 URL 리스트
    private List<String> imageUrls;         // 첨부파일들
}
