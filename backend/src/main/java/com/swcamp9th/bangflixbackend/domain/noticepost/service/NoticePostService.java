package com.swcamp9th.bangflixbackend.domain.noticepost.service;

import com.swcamp9th.bangflixbackend.shared.response.NoticePageResponse;
import com.swcamp9th.bangflixbackend.domain.noticepost.dto.NoticePostCreateDTO;
import com.swcamp9th.bangflixbackend.domain.noticepost.dto.NoticePostDTO;
import com.swcamp9th.bangflixbackend.domain.noticepost.dto.NoticePostUpdateDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NoticePostService {

    void createNoticePost(NoticePostCreateDTO newNotice, List<MultipartFile> images, String userId);

    void updateNoticePost(int noticePostCode, NoticePostUpdateDTO updatedNotice,
                          List<MultipartFile> images, String userId);

    void deleteNoticePost(int noticePostCode, String userId);

    NoticePageResponse getAllNotices(Pageable pageable);

    NoticePostDTO findNoticeByCode(int noticePostCode);
}
