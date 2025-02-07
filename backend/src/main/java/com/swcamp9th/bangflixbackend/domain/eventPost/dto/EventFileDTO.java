package com.swcamp9th.bangflixbackend.domain.eventPost.dto;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EventFileDTO {

    private Integer eventFileCode;          // 첨부파일 코드
    private String url;                     // 파일 url
    private Boolean active;                 // 활성화 여부
    private String createdAt;               // 생성일시(작성일시)
    private Integer eventPostCode;          // 첨부된 게시글 코드
}
