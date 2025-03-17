package com.swcamp9th.bangflixbackend.domain.communitypost.controller;

import com.swcamp9th.bangflixbackend.shared.response.ResponseCode;
import com.swcamp9th.bangflixbackend.shared.response.SuccessResponse;
import com.swcamp9th.bangflixbackend.domain.communitypost.service.CommunityPostService;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostCreateDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<SuccessResponse<Void>> createCommunityPost(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @RequestPart CommunityPostCreateDTO newPost,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        communityPostService.createPost(loginId, newPost, images);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.empty(ResponseCode.CREATED));
    }

    /* 게시글 수정 */
    @PutMapping(
            value = "/post/{communityPostCode}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 수정 API")
    public ResponseEntity<SuccessResponse<Void>> updateCommunityPost(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @PathVariable int communityPostCode,
            @Valid @RequestPart CommunityPostUpdateDTO modifiedPost,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        communityPostService.updatePost(loginId, communityPostCode, modifiedPost, images);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.empty(ResponseCode.UPDATED));
    }

    /* 게시글 삭제 */
    @DeleteMapping("/post/{communityPostCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 삭제 API")
    public ResponseEntity<SuccessResponse<Void>> deleteCommunityPost(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @PathVariable int communityPostCode
    ) {
        communityPostService.deletePost(loginId, communityPostCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.empty(ResponseCode.DELETED));
    }

    /* 게시글 목록 조회 */
    @GetMapping
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 목록 조회 API")
    public ResponseEntity<SuccessResponse<List<CommunityPostDTO>>> getAllPosts(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId) {

        List<CommunityPostDTO> postDTOList = communityPostService.getAllPosts(loginId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, postDTOList));
    }

    /* 게시글 상세 조회 */
    @GetMapping("/post/{communityPostCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 상세 조회 API")
    public ResponseEntity<SuccessResponse<CommunityPostDTO>> findPost(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @PathVariable int communityPostCode
    ) {
        CommunityPostDTO postDTO = communityPostService.findPostByCode(loginId, communityPostCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, postDTO));
    }

    /* 게시글 구독 */
    @GetMapping("/post/subscribe/{communityPostCode}")
    public ResponseEntity<SuccessResponse<Void>> subscribe(
            @PathVariable Integer communityPostCode,
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.empty(ResponseCode.OK));
    }

    /* 내가 작성한 게시글 목록 조회 */
    @GetMapping("/my")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "내가 작성한 커뮤니티 게시글 목록 조회 API")
    public ResponseEntity<SuccessResponse<List<CommunityPostDTO>>> getMyPosts(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId) {

        List<CommunityPostDTO> postDTOList = communityPostService.getMyPosts(loginId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, postDTOList));
    }
}
