package com.swcamp9th.bangflixbackend.domain.noticepost.controller;

import com.swcamp9th.bangflixbackend.shared.response.NoticePageResponse;
import com.swcamp9th.bangflixbackend.shared.response.ResponseCode;
import com.swcamp9th.bangflixbackend.shared.response.SuccessResponse;
import com.swcamp9th.bangflixbackend.domain.noticepost.dto.NoticePostCreateDTO;
import com.swcamp9th.bangflixbackend.domain.noticepost.dto.NoticePostDTO;
import com.swcamp9th.bangflixbackend.domain.noticepost.dto.NoticePostUpdateDTO;
import com.swcamp9th.bangflixbackend.domain.noticepost.service.NoticePostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.swcamp9th.bangflixbackend.shared.filter.RequestFilter.SERVLET_REQUEST_ATTRIBUTE_KEY;

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
    public ResponseEntity<SuccessResponse<Void>> createNoticePost(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @Valid @RequestPart NoticePostCreateDTO newNotice,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        noticePostService.createNoticePost(newNotice, images, loginId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.empty(ResponseCode.CREATED));
    }

    /* 공지사항 게시글 수정 */
    @PutMapping(value = "/post/{noticePostCode}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "공지사항 게시글 수정 API")
    public ResponseEntity<SuccessResponse<Void>> updateNoticePost(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @PathVariable int noticePostCode,
            @Valid @RequestPart NoticePostUpdateDTO updatedNotice,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        noticePostService.updateNoticePost(noticePostCode, updatedNotice, images, loginId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(SuccessResponse.empty(ResponseCode.UPDATED));
    }

    /* 공지사항 게시글 삭제 */
    @DeleteMapping("/post/{noticePostCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "공지사항 게시글 삭제 API")
    public ResponseEntity<SuccessResponse<Void>> deleteNoticePost(@PathVariable int noticePostCode,
                                                                    @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId) {

        noticePostService.deleteNoticePost(noticePostCode, loginId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(SuccessResponse.empty(ResponseCode.DELETED));
    }

    /* 공지사항 게시글 목록 조회(페이지네이션) */
    @GetMapping("")
    @Operation(summary = "공지사항 게시글 목록 조회 API (default size = 6)")
    public ResponseEntity<SuccessResponse<NoticePageResponse>> getNoticePostList(
            @PageableDefault(size = 6) Pageable pageable) {
        NoticePageResponse noticePageInfo = noticePostService.getAllNotices(pageable);
        if (noticePageInfo.getNoticePosts().isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(SuccessResponse.of(ResponseCode.OK, noticePageInfo));
        }
    }

    /* 공지사항 게시글 상세 조회 */
    @GetMapping("/post/{noticePostCode}")
    @Operation(summary = "공지사항 게시글 상세 조회 API")
    public ResponseEntity<SuccessResponse<NoticePostDTO>> getNoticePost(@PathVariable int noticePostCode) {
        NoticePostDTO noticePostDTO = noticePostService.findNoticeByCode(noticePostCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, noticePostDTO));
    }
}
