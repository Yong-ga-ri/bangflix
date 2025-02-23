package com.swcamp9th.bangflixbackend.domain.communitypost.controller;

import com.swcamp9th.bangflixbackend.shared.response.Response;
import com.swcamp9th.bangflixbackend.domain.communitypost.service.CommunityPostService;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostCreateDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.swcamp9th.bangflixbackend.shared.filter.RequestFilter.SERVLET_REQUEST_ATTRIBUTE_KEY;

@Slf4j
@RestController
@RequestMapping("api/v1/community")
public class CommunityController {

    private final CommunityPostService communityPostService;

    @Autowired
    public CommunityController(CommunityPostService communityPostService) {
        this.communityPostService = communityPostService;
    }

    /* 게시글 등록 */
    @PostMapping(
            value = "/post",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 등록 API")
    public ResponseEntity<Response<Object>> createCommunityPost(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @RequestPart CommunityPostCreateDTO newPost,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        communityPostService.createPost(loginId, newPost, images);
        return ResponseEntity.ok(
                new Response<>(200, "게시글 등록 성공", null)
        );
    }

    /* 게시글 수정 */
    @PutMapping(
            value = "/post/{communityPostCode}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 수정 API")
    public ResponseEntity<Response<Object>> updateCommunityPost(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @PathVariable int communityPostCode,
            @Valid @RequestPart CommunityPostUpdateDTO modifiedPost,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        communityPostService.updatePost(loginId, communityPostCode, modifiedPost, images);
        return ResponseEntity.ok(
                new Response<>(200, "게시글 수정 성공", null)
        );
    }

    /* 게시글 삭제 */
    @DeleteMapping("/post/{communityPostCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 삭제 API")
    public ResponseEntity<Response<Object>> deleteCommunityPost(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @PathVariable int communityPostCode
    ) {
        communityPostService.deletePost(loginId, communityPostCode);
        return ResponseEntity.ok(
                new Response<>(200, "게시글 삭제 성공", null)
        );
    }

    /* 게시글 목록 조회 */
    @GetMapping
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 목록 조회 API")
    public ResponseEntity<Response<List<CommunityPostDTO>>> getAllPosts(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId) {

        List<CommunityPostDTO> posts = communityPostService.getAllPosts(loginId);
        return ResponseEntity.ok(
                new Response<>(200, "게시글 목록 조회 성공", posts)
        );
    }

    /* 게시글 상세 조회 */
    @GetMapping("/post/{communityPostCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 상세 조회 API")
    public ResponseEntity<Response<CommunityPostDTO>> findPost(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @PathVariable int communityPostCode
    ) {
        CommunityPostDTO post = communityPostService.findPostByCode(loginId, communityPostCode);
        return ResponseEntity.ok(
                new Response<>(200, "게시글 조회 성공", post)
        );
    }

    /* 게시글 구독 */
    @GetMapping("/post/subscribe/{communityPostCode}")
    public ResponseEntity<Response<String>> subscribe(
            @PathVariable Integer communityPostCode,
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        return ResponseEntity.ok(
                new Response<>(200, "구독 완료", "helloworld")
        );
    }

    /* 내가 작성한 게시글 목록 조회 */
    @GetMapping("/my")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "내가 작성한 커뮤니티 게시글 목록 조회 API")
    public ResponseEntity<Response<List<CommunityPostDTO>>> getMyPosts(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId) {

        List<CommunityPostDTO> myPostList = communityPostService.getMyPosts(loginId);
        return ResponseEntity.ok(
                new Response<>(200, "내가 작성한 게시글 목록 조회 성공", myPostList)
        );
    }
}
