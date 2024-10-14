package com.swcamp9th.bangflixbackend.domain.community.communityPost.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommunityPostUpdateDTO {

    private String title;                   // 제목
    private String content;                 // 내용
    private Integer memberCode;             // 회원 코드(작성자)

    // 수정할 첨부파일 URL 리스트
//    private List<String> imageUrls;         // 첨부파일들
}
