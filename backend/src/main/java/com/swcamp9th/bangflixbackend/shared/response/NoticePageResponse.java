package com.swcamp9th.bangflixbackend.shared.response;

import com.swcamp9th.bangflixbackend.domain.noticePost.dto.NoticePostDTO;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NoticePageResponse {

    private List<NoticePostDTO> noticePosts;
    private int currentPage;
    private int totalPages;
    private long totalElements;
}
