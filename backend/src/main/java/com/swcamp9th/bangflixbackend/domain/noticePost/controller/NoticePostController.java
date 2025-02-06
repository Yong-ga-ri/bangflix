package com.swcamp9th.bangflixbackend.domain.noticePost.controller;

import com.swcamp9th.bangflixbackend.shared.response.NoticePageResponse;
import com.swcamp9th.bangflixbackend.shared.response.ResponseMessage;
import com.swcamp9th.bangflixbackend.domain.noticePost.dto.NoticePostCreateDTO;
import com.swcamp9th.bangflixbackend.domain.noticePost.dto.NoticePostDTO;
import com.swcamp9th.bangflixbackend.domain.noticePost.dto.NoticePostUpdateDTO;
import com.swcamp9th.bangflixbackend.domain.noticePost.service.NoticePostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController("noticePostController")
@Slf4j
@RequestMapping("api/v1/notice")
public class NoticePostController {

    private final NoticePostService noticePostService;

    @Autowired
    public NoticePostController(NoticePostService noticePostService) {
        this.noticePostService = noticePostService;
    }

    /* 공지사항 게시글 등록 */
    @PostMapping(value = "/post", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "공지사항 게시글 등록 API")
    public ResponseEntity<ResponseMessage<Object>> createNoticePost(
            @RequestAttribute("loginId") String loginId,
            @Valid @RequestPart NoticePostCreateDTO newNotice,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        noticePostService.createNoticePost(newNotice, images, loginId);
        return ResponseEntity.ok(new ResponseMessage<>(200, "게시글 등록 성공", null));
    }

    /* 공지사항 게시글 수정 */
    @PutMapping(value = "/post/{noticePostCode}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "공지사항 게시글 수정 API")
    public ResponseEntity<ResponseMessage<Object>> updateNoticePost(
            @RequestAttribute("loginId") String loginId,
            @PathVariable int noticePostCode,
            @Valid @RequestPart NoticePostUpdateDTO updatedNotice,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        noticePostService.updateNoticePost(noticePostCode, updatedNotice, images, loginId);
        return ResponseEntity.ok(new ResponseMessage<>(200, "게시글 수정 성공", null));
    }

    /* 공지사항 게시글 삭제 */
    @DeleteMapping("/post/{noticePostCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "공지사항 게시글 삭제 API")
    public ResponseEntity<ResponseMessage<Object>> deleteNoticePost(@PathVariable int noticePostCode,
                                                                    @RequestAttribute("loginId") String loginId) {

        noticePostService.deleteNoticePost(noticePostCode, loginId);
        return ResponseEntity.ok(new ResponseMessage<>(200, "게시글 삭제 성공", null));
    }

    /* 공지사항 게시글 목록 조회(페이지네이션) */
    @GetMapping("")
    @Operation(summary = "공지사항 게시글 목록 조회 API (default size = 6)")
    public ResponseEntity<ResponseMessage<NoticePageResponse>> getNoticePostList(
            @PageableDefault(size = 6) Pageable pageable) {
        NoticePageResponse noticePageInfo = noticePostService.getAllNotices(pageable);
        if (noticePageInfo.getNoticePosts().isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(new ResponseMessage<>(200, "게시글 목록 조회 성공", noticePageInfo));
        }
    }

    /* 공지사항 게시글 상세 조회 */
    @GetMapping("/post/{noticePostCode}")
    @Operation(summary = "공지사항 게시글 상세 조회 API")
    public ResponseEntity<ResponseMessage<NoticePostDTO>> getNoticePost(@PathVariable int noticePostCode) {
        NoticePostDTO noticePost = noticePostService.findNoticeByCode(noticePostCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "게시글 조회 성공", noticePost));
    }
}
