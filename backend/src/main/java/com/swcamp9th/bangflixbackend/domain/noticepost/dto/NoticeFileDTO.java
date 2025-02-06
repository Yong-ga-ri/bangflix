package com.swcamp9th.bangflixbackend.domain.noticepost.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NoticeFileDTO {

    private Integer noticeFileCode;         // 첨부파일 코드
    private String url;                     // 파일 url
    private Boolean active;                 // 활성화 여부
    private String createdAt;               // 생성일시
    private Integer noticePostCode;         // 첨부된 게시글 코드
}
